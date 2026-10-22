package neatlogic.framework.util;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.file.FileNameNotNullException;
import neatlogic.framework.exception.file.FilePathIllegalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.Assert.*;

/** 使用临时目录验证文件及在线文档资源的安全边界。 */
public class FileSafeUtilTest {
    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    /** 正常读写、绝对路径和待创建的多级目录继续受支持。 */
    @Test
    public void acceptsNormalPathsAndMissingDirectories() throws Exception {
        Path root = temporary.newFolder("root").toPath();
        Path file = Files.write(root.resolve("normal.txt"), new byte[]{1});
        assertEquals(file.toFile(), FileSafeUtil.getDownloadFile("normal.txt", root.toString()));
        assertEquals(file.toFile(), FileSafeUtil.getDownloadFile(file.toString(), root.toString()));
        assertEquals(root.resolve("new/a/b.txt").toFile(), FileSafeUtil.getChildFile(root.toString(), "new/a", "b.txt"));
        Path missingRoot = root.resolve("missing/root");
        assertEquals(missingRoot.resolve("dir/file").toFile(), FileSafeUtil.getChildPath(missingRoot.toString(), "dir/file"));
    }

    /** 遍历、多重编码、绝对越界和同名前缀均不能越过根目录。 */
    @Test
    public void rejectsTraversalAndSiblingPrefix() throws Exception {
        Path root = temporary.newFolder("data").toPath();
        Path sibling = temporary.newFolder("data-other").toPath();
        assertIllegal(() -> FileSafeUtil.getPath("../outside", root.toString()));
        assertIllegal(() -> FileSafeUtil.getPath("%252e%252e%252foutside", root.toString()));
        assertIllegal(() -> FileSafeUtil.getPath(sibling.resolve("file").toString(), root.toString()));
    }

    /** 根目录链接和根内链接正常工作，越界链接在读写前拒绝。 */
    @Test
    public void validatesSymlinksForReadAndCreate() throws Exception {
        Path root = temporary.newFolder("root").toPath();
        Path inside = Files.createDirectory(root.resolve("inside"));
        Files.write(inside.resolve("file.txt"), new byte[]{1});
        Path outside = temporary.newFolder("outside").toPath();
        Files.write(outside.resolve("file.txt"), new byte[]{1});
        Path rootAlias = temporary.getRoot().toPath().resolve("root-alias");
        Files.createSymbolicLink(rootAlias, root);
        Files.createSymbolicLink(root.resolve("allowed"), inside);
        Files.createSymbolicLink(root.resolve("escape"), outside);
        assertEquals(rootAlias.resolve("inside/file.txt").toFile(), FileSafeUtil.getDownloadFile("inside/file.txt", rootAlias.toString()));
        assertEquals(root.resolve("allowed/file.txt").toFile(), FileSafeUtil.getDownloadFile("allowed/file.txt", root.toString()));
        assertEquals(root.resolve("allowed/new/a/file.txt").toFile(), FileSafeUtil.getChildPath(root.toString(), "allowed/new/a/file.txt"));
        assertIllegal(() -> FileSafeUtil.getDownloadFile("escape/file.txt", root.toString()));
        assertIllegal(() -> FileSafeUtil.getChildPath(root.toString(), "escape/new/a/file.txt"));
        assertFalse(FileSafeUtil.isSubPath(root.toFile(), root.resolve("escape/file.txt").toFile()));
    }

    /** 禁止路径无法通过软链接别名或配置别名绕过。 */
    @Test
    public void checksForbiddenLogicalAndRealPaths() throws Exception {
        Path root = temporary.newFolder("root").toPath();
        Path secret = Files.createDirectory(root.resolve("secret"));
        Files.write(secret.resolve("file"), new byte[]{1});
        Files.createSymbolicLink(root.resolve("alias"), secret);
        String previous = Config.getProperties().getProperty("file.safe.forbidden.path");
        try {
            Config.getProperties().setProperty("file.safe.forbidden.path", "secret");
            assertIllegal(() -> FileSafeUtil.getPath("alias/file", root.toString()));
            Config.getProperties().setProperty("file.safe.forbidden.path", "alias");
            assertIllegal(() -> FileSafeUtil.getPath("secret/file", root.toString()));
            assertIllegal(() -> FileSafeUtil.getPath("alias/file", root.toString()));
        } finally {
            if (previous == null) {
                Config.getProperties().remove("file.safe.forbidden.path");
            } else {
                Config.getProperties().setProperty("file.safe.forbidden.path", previous);
            }
        }
    }

    /** 正常目录可递归检查，循环链接及越界链接被拒绝。 */
    @Test
    public void rejectsDirectoryCyclesAndEscapes() throws Exception {
        Path root = temporary.newFolder("root").toPath();
        Path child = Files.createDirectory(root.resolve("child"));
        Files.write(child.resolve("normal"), new byte[]{1});
        Files.createSymbolicLink(root.resolve("alias"), child);
        FileSafeUtil.validateNoForbiddenPathInDirectory(root.toFile(), root.toFile());
        Files.createSymbolicLink(child.resolve("cycle"), root);
        assertIllegal(() -> FileSafeUtil.validateNoForbiddenPathInDirectory(root.toFile(), root.toFile()));
        Files.delete(child.resolve("cycle"));
        Files.createSymbolicLink(child.resolve("escape"), temporary.newFolder("outside").toPath());
        assertIllegal(() -> FileSafeUtil.validateNoForbiddenPathInDirectory(root.toFile(), root.toFile()));
    }

    /** 在线文档路径可读取，其他classpath资源及glob查询不允许。 */
    @Test
    public void restrictsClasspathToDocumentResources() throws Exception {
        String good = "neatlogic/resources/tenant/documentonline/images/test.png";
        assertEquals("classpath:" + good, location(good, temporary.getRoot()));
        assertEquals("classpath:neatlogic/resources/tenant/documentonline/intro.md", location("neatlogic/resources/tenant/documentonline/intro.md", temporary.getRoot()));
        for (String bad : new String[]{"neatlogic/framework/Config.class", "neatlogic/resources/tenant/config.xml", "neatlogic/resources/tenant/documentonline/../config.xml", "neatlogic/resources/tenant/documentonline/*.png", "neatlogic/resources/tenant/documentonline/../../Config.class"}) {
            assertIllegal(() -> location(bad, temporary.getRoot()));
        }
    }

    /** JAR白名单及真实目录边界同时生效，合法JAR资源定位保持兼容。 */
    @Test
    public void validatesJarWhitelistAndRealLocation() throws Exception {
        Path root = temporary.newFolder("jars").toPath();
        Path good = Files.write(root.resolve("neatlogic-doc.jar"), new byte[]{1});
        String entry = "!/neatlogic/resources/tenant/documentonline/test.png";
        assertEquals("jar:file:" + good + entry, location("jar:file:" + good + entry, root.toFile()));
        Path outside = Files.write(temporary.getRoot().toPath().resolve("neatlogic-out.jar"), new byte[]{1});
        Files.createSymbolicLink(root.resolve("neatlogic-link.jar"), outside);
        Path other = Files.write(root.resolve("other.jar"), new byte[]{1});
        Path zip = Files.write(root.resolve("neatlogic-doc.zip"), new byte[]{1});
        assertIllegal(() -> location("jar:file:" + outside + entry, root.toFile()));
        assertIllegal(() -> location("jar:file:" + root.resolve("neatlogic-link.jar") + entry, root.toFile()));
        assertIllegal(() -> location("jar:file:" + other + entry, root.toFile()));
        assertIllegal(() -> location("jar:file:" + zip + entry, root.toFile()));
        assertIllegal(() -> location("jar:file:" + good + "!/neatlogic/framework/Config.class", root.toFile()));
    }

    /** 浏览器带目录的上传名称只保留文件名，空名称和特殊名称拒绝。 */
    @Test
    public void extractsSafeUploadName() {
        assertEquals("图片.png", FileSafeUtil.getUploadFileName("C:\\fakepath\\图片.png"));
        assertEquals("a.txt", FileSafeUtil.getUploadFileName("../../a.txt"));
        for (String bad : new String[]{"", " ", ".", "..", "a/", "a/..", "a\0/b"}) {
            try {
                FileSafeUtil.getUploadFileName(bad);
                fail("应拒绝文件名：" + bad);
            } catch (FileNameNotNullException expected) {
                assertNotNull(expected);
            }
        }
    }

    /** 使用固定白名单生成资源定位串。 */
    private String location(String path, File root) {
        return FileSafeUtil.getSafeClasspathResourceLocationPattern(path, root.getPath(), Collections.singletonList("neatlogic-"), ".jar", "neatlogic/");
    }

    /** 确认安全校验拒绝危险路径。 */
    private void assertIllegal(CheckedAction action) throws Exception {
        try {
            action.run();
            fail("应拒绝不安全路径");
        } catch (FilePathIllegalException expected) {
            assertNotNull(expected);
        }
    }

    /** 允许测试调用带IO异常的方法。 */
    private interface CheckedAction {
        void run() throws IOException;
    }
}

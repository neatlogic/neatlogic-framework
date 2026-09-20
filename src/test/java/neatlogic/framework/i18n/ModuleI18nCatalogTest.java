package neatlogic.framework.i18n;

import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** 验证模块语言资源发现、合并和启动期校验。 */
public class ModuleI18nCatalogTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /** 模块资源应被发现，不支持的语言应整体回退中文。 */
    @Test
    public void loadsModuleResourcesAndFallsBackToChinese() throws Exception {
        Path root = temporaryFolder.newFolder("catalog").toPath();
        writeBundle(root, "sample", "zh", "{\"sample\":{\"name\":\"示例\"}}");
        writeBundle(root, "sample", "en", "{\"sample\":{\"name\":\"Sample\"}}");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            ModuleI18nCatalog catalog = new ModuleI18nCatalog(classLoader);
            assertEquals("Sample", catalog.findMessage("en", "sample.name"));
            assertEquals("示例", catalog.findMessage("zh", "sample.name"));
            assertTrue(catalog.getOrigin("zh", "sample.name").contains("/sample/i18n/language_zh.json"));
            assertTrue(catalog.getOwnerLanguageMessageMap().containsKey("sample"));
        }
    }

    /** 两个模块声明相同完整 key 时必须阻断启动。 */
    @Test(expected = ModuleInitRuntimeException.class)
    public void rejectsDuplicateKeysAcrossModules() throws Exception {
        Path root = temporaryFolder.newFolder("duplicate").toPath();
        writeBundle(root, "first", "zh", "{\"duplicate\":\"第一项\"}");
        writeBundle(root, "first", "en", "{\"duplicate\":\"First\"}");
        writeBundle(root, "second", "zh", "{\"duplicate\":\"第二项\"}");
        writeBundle(root, "second", "en", "{\"duplicate\":\"Second\"}");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            new ModuleI18nCatalog(classLoader);
        }
    }

    /** 非字符串叶子节点必须在启动阶段报告配置错误。 */
    @Test(expected = ModuleInitRuntimeException.class)
    public void rejectsNonStringLeaf() throws Exception {
        Path root = temporaryFolder.newFolder("non-string").toPath();
        writeBundle(root, "sample", "zh", "{\"sample\":1}");
        writeBundle(root, "sample", "en", "{\"sample\":\"Sample\"}");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            new ModuleI18nCatalog(classLoader);
        }
    }

    /** JSON 文件内部存在重复字段时必须阻断启动。 */
    @Test(expected = ModuleInitRuntimeException.class)
    public void rejectsDuplicateJsonFields() throws Exception {
        Path root = temporaryFolder.newFolder("duplicate-json").toPath();
        writeBundle(root, "sample", "zh", "{\"sample\":\"一\",\"sample\":\"二\"}");
        writeBundle(root, "sample", "en", "{\"sample\":\"Sample\"}");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            new ModuleI18nCatalog(classLoader);
        }
    }

    /** 字段名包含点号时会与嵌套路径混淆，必须在启动阶段拒绝。 */
    @Test(expected = ModuleInitRuntimeException.class)
    public void rejectsDottedFieldNames() throws Exception {
        Path root = temporaryFolder.newFolder("dotted-field").toPath();
        writeBundle(root, "sample", "zh", "{\"sample.name\":\"示例\"}");
        writeBundle(root, "sample", "en", "{\"sample.name\":\"Sample\"}");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            new ModuleI18nCatalog(classLoader);
        }
    }

    /** 空字段名无法形成稳定路径，必须在启动阶段拒绝。 */
    @Test(expected = ModuleInitRuntimeException.class)
    public void rejectsEmptyFieldNames() throws Exception {
        Path root = temporaryFolder.newFolder("empty-field").toPath();
        writeBundle(root, "sample", "zh", "{\"\":\"示例\"}");
        writeBundle(root, "sample", "en", "{\"\":\"Sample\"}");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            new ModuleI18nCatalog(classLoader);
        }
    }

    /** 畸形编号参数不能因存在其他大括号而跳过校验。 */
    @Test(expected = ModuleInitRuntimeException.class)
    public void rejectsMalformedMessageFormat() throws Exception {
        Path root = temporaryFolder.newFolder("malformed-template").toPath();
        writeBundle(root, "sample", "zh", "{\"sample\":\"第{1st}项属于{0}\"}");
        writeBundle(root, "sample", "en", "{\"sample\":\"Item {1st} belongs to {0}\"}");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            new ModuleI18nCatalog(classLoader);
        }
    }

    /** 中英文引用不同参数索引时必须阻断启动。 */
    @Test(expected = ModuleInitRuntimeException.class)
    public void rejectsArgumentMismatch() throws Exception {
        Path root = temporaryFolder.newFolder("argument-mismatch").toPath();
        writeBundle(root, "sample", "zh", "{\"sample\":\"{0}-{1}\"}");
        writeBundle(root, "sample", "en", "{\"sample\":\"{0}\"}");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            new ModuleI18nCatalog(classLoader);
        }
    }

    /** 参数索引存在空洞时必须阻断启动。 */
    @Test(expected = ModuleInitRuntimeException.class)
    public void rejectsNonContinuousArguments() throws Exception {
        Path root = temporaryFolder.newFolder("argument-gap").toPath();
        writeBundle(root, "sample", "zh", "{\"sample\":\"{0}-{2}\"}");
        writeBundle(root, "sample", "en", "{\"sample\":\"{0}-{2}\"}");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{root.toUri().toURL()}, null)) {
            new ModuleI18nCatalog(classLoader);
        }
    }

    /** 打包在 JAR 中的模块资源必须与目录资源采用相同发现规则。 */
    @Test
    public void loadsResourcesFromJar() throws Exception {
        Path jar = temporaryFolder.newFile("module.jar").toPath();
        try (JarOutputStream outputStream = new JarOutputStream(Files.newOutputStream(jar))) {
            writeJarDirectory(outputStream, "neatlogic/");
            writeJarDirectory(outputStream, "neatlogic/resources/");
            writeJarDirectory(outputStream, "neatlogic/resources/jar-test/");
            writeJarDirectory(outputStream, "neatlogic/resources/jar-test/i18n/");
            writeJarEntry(outputStream, "neatlogic/resources/jar-test/i18n/language_zh.json",
                    "{\"jar\":{\"name\":\"JAR 模块\"}}");
            writeJarEntry(outputStream, "neatlogic/resources/jar-test/i18n/language_en.json",
                    "{\"jar\":{\"name\":\"JAR Module\"}}");
        }
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jar.toUri().toURL()}, null)) {
            ModuleI18nCatalog catalog = new ModuleI18nCatalog(classLoader);
            assertEquals("JAR Module", catalog.findMessage("en", "jar.name"));
        }
    }

    /** 写入符合模块资源契约的测试语言文件。 */
    private void writeBundle(Path root, String owner, String language, String content) throws Exception {
        Path directory = root.resolve("neatlogic/resources/" + owner + "/i18n");
        Files.createDirectories(directory);
        Files.writeString(directory.resolve("language_" + language + ".json"), content, StandardCharsets.UTF_8);
    }

    /** 向测试 JAR 写入 UTF-8 语言资源。 */
    private void writeJarEntry(JarOutputStream outputStream, String name, String content) throws Exception {
        outputStream.putNextEntry(new JarEntry(name));
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
        outputStream.closeEntry();
    }

    /** 向测试 JAR 写入目录项，模拟 Maven 生成的标准资源 JAR。 */
    private void writeJarDirectory(JarOutputStream outputStream, String name) throws Exception {
        outputStream.putNextEntry(new JarEntry(name));
        outputStream.closeEntry();
    }
}

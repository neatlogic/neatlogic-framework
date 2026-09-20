package neatlogic.framework.i18n;

import neatlogic.framework.exception.module.ModuleInitRuntimeException;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 在构建阶段使用运行时加载器校验工作区内的模块语言资源。
 */
public final class ModuleI18nBuildValidator {
    private static final Set<String> IGNORED_DIRECTORIES = Set.of(
            ".git", ".idea", "node_modules", "target", "dist", "build", "__pycache__");

    /** 工具类不允许实例化。 */
    private ModuleI18nBuildValidator() {
    }

    /**
     * 收集工作区语言资源根目录，并交给运行时目录加载器执行权威校验。
     *
     * @param args 第一个参数为工作区目录
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new ModuleInitRuntimeException("构建期语言资源校验需要一个工作区目录参数");
        }
        Path workspace = Path.of(args[0]).toAbsolutePath().normalize();
        if (!Files.isDirectory(workspace)) {
            throw new ModuleInitRuntimeException("构建期语言资源校验目录不存在，workspace: " + workspace);
        }
        Set<URL> resourceRoots = collectResourceRoots(workspace);
        if (resourceRoots.isEmpty()) {
            throw new ModuleInitRuntimeException("工作区未找到模块语言资源，workspace: " + workspace);
        }
        try (URLClassLoader classLoader = new URLClassLoader(resourceRoots.toArray(URL[]::new), null)) {
            ModuleI18nCatalog catalog = new ModuleI18nCatalog(classLoader);
            int keyCount = catalog.getOwnerLanguageMessageMap().values().stream()
                    .mapToInt(languageMap -> languageMap.get("zh").size())
                    .sum();
            System.out.printf("模块语言资源 Java 校验通过，moduleCount: %d, keyCount: %d%n",
                    catalog.getOwnerLanguageMessageMap().size(), keyCount);
        }
    }

    /** 收集包含规范语言文件的 src/main/resources 目录。 */
    private static Set<URL> collectResourceRoots(Path workspace) throws IOException {
        Set<URL> resourceRoots = new LinkedHashSet<>();
        Files.walkFileTree(workspace, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) {
                if (!directory.equals(workspace)
                        && IGNORED_DIRECTORIES.contains(directory.getFileName().toString())) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                String filename = file.getFileName().toString();
                if (("language_zh.json".equals(filename) || "language_en.json".equals(filename))
                        && isModuleLanguageResource(file)) {
                    Path resourceRoot = file.getParent().getParent().getParent().getParent().getParent();
                    resourceRoots.add(resourceRoot.toUri().toURL());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return resourceRoots;
    }

    /** 检查文件是否位于 neatlogic/resources/{owner}/i18n 规范路径。 */
    private static boolean isModuleLanguageResource(Path file) {
        Path i18nDirectory = file.getParent();
        Path ownerDirectory = i18nDirectory == null ? null : i18nDirectory.getParent();
        Path resourcesDirectory = ownerDirectory == null ? null : ownerDirectory.getParent();
        Path neatlogicDirectory = resourcesDirectory == null ? null : resourcesDirectory.getParent();
        Path resourceRoot = neatlogicDirectory == null ? null : neatlogicDirectory.getParent();
        return i18nDirectory != null && "i18n".equals(i18nDirectory.getFileName().toString())
                && resourcesDirectory != null && "resources".equals(resourcesDirectory.getFileName().toString())
                && neatlogicDirectory != null && "neatlogic".equals(neatlogicDirectory.getFileName().toString())
                && resourceRoot != null && "resources".equals(resourceRoot.getFileName().toString())
                && resourceRoot.getParent() != null && "main".equals(resourceRoot.getParent().getFileName().toString())
                && resourceRoot.getParent().getParent() != null
                && "src".equals(resourceRoot.getParent().getParent().getFileName().toString());
    }
}

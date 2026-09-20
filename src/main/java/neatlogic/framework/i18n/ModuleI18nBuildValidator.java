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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 在构建阶段使用运行时加载器校验工作区内的模块语言资源。
 */
public final class ModuleI18nBuildValidator {
    private static final Set<String> IGNORED_DIRECTORIES = Set.of(
            ".git", ".idea", "node_modules", "target", "dist", "build", "__pycache__");
    private static final Pattern MODULE_PATTERN = Pattern.compile("<module>\\s*([^<]+?)\\s*</module>");

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
        ValidationSummary summary = validateWorkspace(Path.of(args[0]));
        System.out.printf("模块语言资源 Java 校验通过，moduleCount: %d, languageFileCount: %d, keyCount: %d%n",
                summary.moduleCount(), summary.languageFileCount(), summary.keyCount());
    }

    /** 校验工作区资源布局，并使用运行时加载器检查语言资源内容。 */
    static ValidationSummary validateWorkspace(Path workspacePath) throws Exception {
        Path workspace = workspacePath.toAbsolutePath().normalize();
        if (!Files.isDirectory(workspace)) {
            throw new ModuleInitRuntimeException("构建期语言资源校验目录不存在，workspace: " + workspace);
        }
        WorkspaceResources resources = collectWorkspaceResources(workspace);
        if (resources.resourceRoots().isEmpty()) {
            throw new ModuleInitRuntimeException("工作区未找到模块语言资源，workspace: " + workspace);
        }
        validateModuleCoverage(workspace, resources.moduleDirectories());
        try (URLClassLoader classLoader = new URLClassLoader(resources.resourceRoots().toArray(URL[]::new), null)) {
            ModuleI18nCatalog catalog = new ModuleI18nCatalog(classLoader);
            int keyCount = catalog.getOwnerLanguageMessageMap().values().stream()
                    .mapToInt(languageMap -> languageMap.get("zh").size())
                    .sum();
            return new ValidationSummary(catalog.getOwnerLanguageMessageMap().size(),
                    resources.languageFileCount(), keyCount);
        }
    }

    /** 收集规范语言文件，并校验资源 owner 与模块目录一致。 */
    private static WorkspaceResources collectWorkspaceResources(Path workspace) throws IOException {
        Set<URL> resourceRoots = new LinkedHashSet<>();
        Set<Path> moduleDirectories = new LinkedHashSet<>();
        int[] languageFileCount = {0};
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
                if (!"language_zh.json".equals(filename) && !"language_en.json".equals(filename)) {
                    return FileVisitResult.CONTINUE;
                }
                Path resourceRoot = findMainResourceRoot(file);
                if (resourceRoot == null) {
                    return FileVisitResult.CONTINUE;
                }
                Path moduleDirectory = resourceRoot.getParent().getParent().getParent();
                if (!isModuleLanguageResource(file)) {
                    throw new ModuleInitRuntimeException("模块语言资源路径不符合规范，resource: " + file);
                }
                String owner = file.getParent().getParent().getFileName().toString();
                String expectedOwner = moduleDirectory.getFileName().toString().replaceFirst("^neatlogic-", "");
                if (!owner.equals(expectedOwner)) {
                    throw new ModuleInitRuntimeException("语言资源 owner 与 artifact 目录不一致，resource: " + file
                            + ", expected: " + expectedOwner + ", actual: " + owner);
                }
                resourceRoots.add(resourceRoot.toUri().toURL());
                moduleDirectories.add(moduleDirectory.toAbsolutePath().normalize());
                languageFileCount[0]++;
                return FileVisitResult.CONTINUE;
            }
        });
        return new WorkspaceResources(resourceRoots, moduleDirectories, languageFileCount[0]);
    }

    /** 返回文件所属的 src/main/resources；其他位置的同名文件不属于模块语言资源。 */
    private static Path findMainResourceRoot(Path file) {
        Path current = file.getParent();
        while (current != null) {
            if (current.getFileName() != null
                    && "resources".equals(current.getFileName().toString())
                    && current.getParent() != null
                    && "main".equals(current.getParent().getFileName().toString())
                    && current.getParent().getParent() != null
                    && "src".equals(current.getParent().getParent().getFileName().toString())) {
                return current;
            }
            current = current.getParent();
        }
        return null;
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

    /** 检查 Reactor 中带 servlet 描述的模块是否完全遗漏语言资源。 */
    private static void validateModuleCoverage(Path workspace, Set<Path> bundledModuleDirectories) throws IOException {
        Set<Path> missingModuleDirectories = new LinkedHashSet<>();
        for (Path moduleDirectory : findReactorModuleDirectories(workspace)) {
            if (!bundledModuleDirectories.contains(moduleDirectory) && containsModuleDescriptor(moduleDirectory)) {
                missingModuleDirectories.add(moduleDirectory);
            }
        }
        if (!missingModuleDirectories.isEmpty()) {
            throw new ModuleInitRuntimeException("模块同时缺少中英文语言资源: " + missingModuleDirectories);
        }
    }

    /** 从聚合 POM 收集全部已存在的模块目录。 */
    private static Set<Path> findReactorModuleDirectories(Path workspace) throws IOException {
        Path buildRootPom = workspace.resolve("neatlogic-build-root/pom.xml");
        Set<Path> moduleDirectories = new LinkedHashSet<>();
        if (!Files.isRegularFile(buildRootPom)) {
            return moduleDirectories;
        }
        Matcher matcher = MODULE_PATTERN.matcher(Files.readString(buildRootPom));
        while (matcher.find()) {
            Path moduleDirectory = buildRootPom.getParent().resolve(matcher.group(1).trim())
                    .toAbsolutePath().normalize();
            if (Files.isDirectory(moduleDirectory)) {
                moduleDirectories.add(moduleDirectory);
            }
        }
        return moduleDirectories;
    }

    /** 判断模块源码中是否声明了模块 servlet 上下文。 */
    private static boolean containsModuleDescriptor(Path moduleDirectory) throws IOException {
        Path sourceDirectory = moduleDirectory.resolve("src/main/java");
        if (!Files.isDirectory(sourceDirectory)) {
            return false;
        }
        try (Stream<Path> files = Files.walk(sourceDirectory)) {
            return files.anyMatch(file -> Files.isRegularFile(file)
                    && file.getFileName().toString().endsWith("-servlet-context.xml"));
        }
    }

    /** 工作区扫描结果。 */
    private record WorkspaceResources(Set<URL> resourceRoots, Set<Path> moduleDirectories, int languageFileCount) {
    }

    /** 构建校验摘要。 */
    record ValidationSummary(int moduleCount, int languageFileCount, int keyCount) {
    }
}

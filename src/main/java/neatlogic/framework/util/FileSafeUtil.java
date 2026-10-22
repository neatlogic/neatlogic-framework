package neatlogic.framework.util;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.file.FileNameNotNullException;
import neatlogic.framework.exception.file.FileNotFoundException;
import neatlogic.framework.exception.file.FilePathIllegalException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.*;

public final class FileSafeUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileSafeUtil.class);
    private static final String FORBIDDEN_PATH_CONFIG_KEY = "file.safe.forbidden.path";

    private FileSafeUtil() {
    }

    /**
     * 从上传文件名中提取真实文件名，去掉客户端可能带上的目录部分。
     */
    public static String getUploadFileName(String fileName) {
        if (StringUtils.isBlank(fileName) || fileName.indexOf('\0') >= 0) {
            throw new FileNameNotNullException();
        }
        String normalizedFileName = fileName.replace("\\", "/");
        String name = normalizedFileName.substring(normalizedFileName.lastIndexOf('/') + 1);
        if (StringUtils.isBlank(name) || ".".equals(name) || "..".equals(name) || name.indexOf('\0') >= 0) {
            throw new FileNameNotNullException();
        }
        return name;
    }

    /**
     * 在指定根目录下拼接相对目录和文件名，同时校验逻辑路径与真实目标边界。
     */
    public static File getChildFile(String rootPath, String relativePath, String fileName) throws IOException {
        File root = getNormalizedAbsoluteFile(rootPath);
        File directory = root;
        if (StringUtils.isNotBlank(relativePath)) {
            directory = resolveNormalizedFile(root, relativePath);
        }
        File file = resolveNormalizedFile(directory, fileName);
        validateLexicalSubPath(root, directory);
        validateLexicalSubPath(root, file);
        validateFilePathAllowed(root, directory);
        validateFilePathAllowed(root, file);
        return file;
    }

    /**
     * 在指定根目录下解析子路径，同时校验逻辑路径与真实目标边界。
     */
    public static File getChildPath(String rootPath, String childPath) throws IOException {
        File root = getNormalizedAbsoluteFile(rootPath);
        File file = resolveNormalizedFile(root, StringUtils.defaultString(childPath));
        validateLexicalSubPath(root, file);
        validateFilePathAllowed(root, file);
        return file;
    }

    /**
     * 获取可下载文件，要求路径合法、文件存在且不是目录。
     */
    public static File getDownloadFile(String path, String rootPath) throws IOException {
        File file = getPath(path, rootPath);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException(FileNotFoundException.Type.NONEXISTENT, path);
        }
        return file;
    }

    /**
     * 以rootPath作为安全边界解析文件路径，绝对路径和软链接真实目标均不得越界。
     */
    public static File getPath(String path, String rootPath) throws IOException {
        if (StringUtils.isBlank(path)) {
            throw new FilePathIllegalException(path);
        }
        File root = getNormalizedAbsoluteFile(rootPath);
        File file = resolveNormalizedFile(root, decode(path));
        // 先校验逻辑路径，再校验真实目标；新建路径检查最近已存在父目录。
        validateLexicalSubPath(root, file);
        validateFilePathAllowed(root, file);
        return file;
    }

    /**
     * 获取已存在的合法目录，适用于保存目录、打包目录等目录型入参。
     */
    public static File getValidatedDirectory(String path, String rootPath) throws IOException {
        File directory = getPath(path, rootPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new FileNotFoundException(FileNotFoundException.Type.NONEXISTENT, path);
        }
        return directory;
    }

    /**
     * 获取已存在的合法路径，文件或目录均可，但规范化路径必须在rootPath内。
     */
    public static File getValidatedPath(String path, String rootPath) throws IOException {
        File file = getPath(path, rootPath);
        if (!file.exists()) {
            throw new FileNotFoundException(FileNotFoundException.Type.NONEXISTENT, path);
        }
        return file;
    }

    /**
     * 在已校验目录下生成子文件路径，并确保文件父目录没有被fileName中的路径片段改变。
     */
    public static File getValidatedChildFile(File directory, String fileName, String rootPath) throws IOException {
        File root = getNormalizedAbsoluteFile(rootPath);
        File normalizedDirectory = getNormalizedAbsoluteFile(directory.getPath());
        File file = resolveNormalizedFile(normalizedDirectory, fileName);
        if (!isLexicalSubPath(root, normalizedDirectory)
                || !isLexicalSubPath(root, file)
                || file.getParentFile() == null
                || !normalizedDirectory.toPath().equals(file.getParentFile().toPath())) {
            throw new FilePathIllegalException(file.getPath());
        }
        validateFilePathAllowed(root, normalizedDirectory);
        validateFilePathAllowed(root, file);
        return file;
    }

    /**
     * 递归校验打包内容的边界和禁止路径，并拒绝软链接形成的祖先目录循环。
     */
    public static void validateNoForbiddenPathInDirectory(File root, File directory) throws IOException {
        validateDirectory(root, directory, new HashSet<>());
    }

    /** 校验目录分支，按真实路径记录当前祖先，允许同一安全目标通过不同别名访问。 */
    private static void validateDirectory(File root, File directory, Set<Path> ancestorPaths) throws IOException {
        if (directory == null) {
            return;
        }
        File normalizedRoot = getNormalizedAbsoluteFile(root.getPath());
        File normalizedFile = getNormalizedAbsoluteFile(directory.getPath());
        validateLexicalSubPath(normalizedRoot, normalizedFile);
        validateFilePathAllowed(normalizedRoot, normalizedFile);
        if (!normalizedFile.isDirectory()) {
            return;
        }
        Path realPath = normalizedFile.toPath().toRealPath();
        if (!ancestorPaths.add(realPath)) {
            throw new FilePathIllegalException(directory.getPath());
        }
        try {
            File[] children = normalizedFile.listFiles();
            if (children == null) {
                throw new IOException("无法读取目录：" + directory.getPath());
            }
            for (File child : children) {
                validateDirectory(normalizedRoot, child, ancestorPaths);
            }
        } finally {
            ancestorPaths.remove(realPath);
        }
    }

    /**
     * 同时检查真实目标边界以及逻辑路径、真实路径对应的禁止访问配置。
     */
    public static void validateFilePathAllowed(File root, File file) throws IOException {
        if (root == null || file == null) {
            return;
        }
        File normalizedRoot = getNormalizedAbsoluteFile(root.getPath());
        Path filePath = file.toPath().toAbsolutePath().normalize();
        Path realFilePath = resolveRealPath(filePath);
        if (!realFilePath.startsWith(resolveRealPath(normalizedRoot.toPath()))) {
            throw new FilePathIllegalException(file.getPath());
        }
        for (File forbiddenPath : getForbiddenPathList(normalizedRoot)) {
            Path forbidden = forbiddenPath.toPath();
            Path realForbidden = resolveRealPath(forbidden);
            if (filePath.startsWith(forbidden) || realFilePath.startsWith(realForbidden)) {
                throw new FilePathIllegalException(file.getPath());
            }
        }
    }

    /**
     * 清洗跨runner、虚拟资源等非本机真实文件路径，返回不含前导斜杠的安全相对路径。
     */
    public static String getSafeRelativePath(String path) {
        String normalizedPath;
        try {
            normalizedPath = decode(path).replace("\\", "/");
        } catch (IOException ex) {
            logger.error("decode safe relative path failed, path: {}", path, ex);
            throw new FilePathIllegalException(path);
        }
        while (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }
        if (StringUtils.isBlank(normalizedPath) || ".".equals(normalizedPath)) {
            return StringUtils.EMPTY;
        }
        String[] pathList = normalizedPath.split("/");
        List<String> safePathList = new ArrayList<>();
        for (String item : pathList) {
            if (StringUtils.isBlank(item) || ".".equals(item)) {
                continue;
            }
            if ("..".equals(item) || item.contains(":")) {
                throw new FilePathIllegalException(path);
            }
            safePathList.add(item);
        }
        // 供跨runner转发的虚拟资源路径使用：统一拒绝../和协议/盘符路径，接收端再校验规范化逻辑路径边界。
        return String.join("/", safePathList);
    }

    /**
     * 生成安全的classpath或jar:file资源定位串，限制普通资源前缀和Jar所在根目录。
     */
    public static String getSafeClasspathResourceLocationPattern(String filePath, String jarRootPath, Collection<String> allowedJarNamePrefixList, String jarSuffix, String classpathRootPrefix) {
        String path;
        try {
            path = decode(filePath).replace("\\", "/");
        } catch (IOException ex) {
            logger.error("decode classpath resource path failed, path: {}", filePath, ex);
            throw new FilePathIllegalException(filePath);
        }
        if (StringUtils.isBlank(path) || path.contains("../") || path.endsWith("/..") || path.contains("%")) {
            throw new FilePathIllegalException(filePath);
        }
        if (path.startsWith("jar:file:")) {
            int separatorIndex = path.indexOf("!/");
            if (separatorIndex == -1) {
                throw new FilePathIllegalException(filePath);
            }
            String jarPath = path.substring("jar:file:".length(), separatorIndex);
            String entryPath = path.substring(separatorIndex + 2);
            File jarRoot = getNormalizedAbsoluteFile(jarRootPath);
            File jarFile = getNormalizedAbsoluteFile(jarPath);
            String jarFileName = jarFile.getName();
            // 仅允许在线文档目录中的白名单Jar，软链接目标也必须位于配置目录中。
            try {
                if (!jarFile.isFile()
                        || !StringUtils.endsWith(jarFileName, jarSuffix)
                        || !isAllowedFileNamePrefix(jarFileName, allowedJarNamePrefixList)
                        || !isSubPath(jarRoot, jarFile)
                        || !isSafeClasspathResourcePath(entryPath, classpathRootPrefix)) {
                    throw new FilePathIllegalException(filePath);
                }
                validateFilePathAllowed(jarRoot, jarFile);
            } catch (IOException ex) {
                throw new FilePathIllegalException(filePath);
            }
            return "jar:file:" + jarFile.getPath().replace("\\", "/") + "!/" + entryPath.replace("\\", "/");
        }
        if (!isSafeClasspathResourcePath(path, classpathRootPrefix)) {
            throw new FilePathIllegalException(filePath);
        }
        return "classpath:" + path;
    }

    /**
     * 校验classpath资源路径，只允许指定根前缀下的相对资源。
     */
    public static boolean isSafeClasspathResourcePath(String path, String classpathRootPrefix) {
        String normalizedPath = StringUtils.defaultString(path).replace("\\", "/");
        String normalizedRootPrefix = StringUtils.defaultString(classpathRootPrefix).replace("\\", "/");
        if (StringUtils.isBlank(normalizedRootPrefix)
                || !normalizedPath.startsWith(normalizedRootPrefix)
                || !normalizedPath.startsWith("neatlogic/resources/")
                || normalizedPath.contains(":") || normalizedPath.indexOf('\0') >= 0
                || normalizedPath.contains("*") || normalizedPath.contains("?")
                || normalizedPath.contains("#") || normalizedPath.contains("!")) {
            return false;
        }
        String[] segments = normalizedPath.split("/", -1);
        boolean documentDirectory = false;
        for (int i = 0; i < segments.length; i++) {
            if (StringUtils.isBlank(segments[i]) || ".".equals(segments[i]) || "..".equals(segments[i])) {
                return false;
            }
            if (i >= 2 && i < segments.length - 1 && "documentonline".equals(segments[i])) {
                documentDirectory = true;
            }
        }
        return documentDirectory;
    }

    /**
     * 校验文件名是否命中允许的前缀白名单。
     */
    private static boolean isAllowedFileNamePrefix(String fileName, Collection<String> allowedPrefixList) {
        if (StringUtils.isBlank(fileName) || allowedPrefixList == null || allowedPrefixList.isEmpty()) {
            return false;
        }
        for (String allowedPrefix : allowedPrefixList) {
            if (StringUtils.isNotBlank(allowedPrefix) && fileName.startsWith(allowedPrefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从配置读取禁止访问的本地路径，支持相对业务根目录或绝对路径，默认空列表保持兼容。
     */
    private static List<File> getForbiddenPathList(File root) throws IOException {
        String pathConfig = StringUtils.EMPTY;
        if (Config.getProperties() != null) {
            pathConfig = Config.getProperties().getProperty(FORBIDDEN_PATH_CONFIG_KEY, StringUtils.EMPTY);
        }
        List<File> pathList = new ArrayList<>();
        if (StringUtils.isBlank(pathConfig)) {
            return pathList;
        }
        String[] pathArray = pathConfig.split(",");
        for (String path : pathArray) {
            String normalizedPath = StringUtils.trimToEmpty(path);
            if (StringUtils.isBlank(normalizedPath)) {
                continue;
            }
            File forbiddenPath = new File(normalizedPath);
            if (!forbiddenPath.isAbsolute()) {
                forbiddenPath = new File(root, normalizedPath);
            }
            pathList.add(getNormalizedAbsoluteFile(forbiddenPath.getPath()));
        }
        return pathList;
    }

    /**
     * 同时判断file的规范化逻辑路径及真实目标是否位于root路径下。
     */
    public static boolean isSubPath(File root, File file) throws IOException {
        Path rootPath = root.toPath().toAbsolutePath().normalize();
        Path filePath = file.toPath().toAbsolutePath().normalize();
        return filePath.startsWith(rootPath) && resolveRealPath(filePath).startsWith(resolveRealPath(rootPath));
    }

    /**
     * 解析已有目标的真实路径；待创建路径通过最近已存在父目录验证，拒绝失效软链接。
     */
    private static Path resolveRealPath(Path path) throws IOException {
        Path absolute = path.toAbsolutePath().normalize();
        Path existing = absolute;
        while (existing != null && !Files.exists(existing, LinkOption.NOFOLLOW_LINKS)) {
            existing = existing.getParent();
        }
        if (existing == null) {
            throw new IOException("无法解析路径：" + path);
        }
        return existing.toRealPath().resolve(existing.relativize(absolute)).normalize();
    }

    /**
     * 转换为规范化绝对路径，不解析软链接真实目标。
     */
    private static File getNormalizedAbsoluteFile(String path) {
        return new File(path).toPath().toAbsolutePath().normalize().toFile();
    }

    /** 按根目录解析相对路径并规范化，保留绝对路径供边界校验。 */
    private static File resolveNormalizedFile(File root, String childPath) {
        Path child = new File(StringUtils.defaultString(childPath)).toPath();
        Path resolvedPath = child;
        if (!child.isAbsolute()) {
            resolvedPath = root.toPath().resolve(child);
        }
        return resolvedPath.toAbsolutePath().normalize().toFile();
    }

    /** 拒绝逻辑路径越界，防止根目录同名前缀混淆。 */
    private static void validateLexicalSubPath(File root, File file) {
        if (!isLexicalSubPath(root, file)) {
            throw new FilePathIllegalException(file.getPath());
        }
    }

    /** 按路径片段判断逻辑边界。 */
    private static boolean isLexicalSubPath(File root, File file) {
        Path rootPath = root.toPath().toAbsolutePath().normalize();
        Path filePath = file.toPath().toAbsolutePath().normalize();
        return filePath.startsWith(rootPath);
    }

    /**
     * 最多解码三次路径参数，用于处理多重URL编码的绕过尝试。
     */
    public static String decode(String path) throws IOException {
        String decoded = StringUtils.defaultString(path);
        for (int i = 0; i < 3; i++) {
            String next = URLDecoder.decode(decoded, StandardCharsets.UTF_8.name());
            if (next.equals(decoded)) {
                break;
            }
            decoded = next;
        }
        return decoded;
    }
}

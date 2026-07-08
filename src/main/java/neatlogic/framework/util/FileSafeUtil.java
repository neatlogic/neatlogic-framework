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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FileSafeUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileSafeUtil.class);
    private static final String FORBIDDEN_PATH_CONFIG_KEY = "file.safe.forbidden.path";

    private FileSafeUtil() {
    }

    /**
     * 从上传文件名中提取真实文件名，去掉客户端可能带上的目录部分。
     */
    public static String getUploadFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new FileNameNotNullException();
        }
        String normalizedFileName = fileName.replace("\\", "/");
        return new File(normalizedFileName).getName();
    }

    /**
     * 在指定根目录下拼接相对目录和文件名，并校验最终真实路径仍在根目录内。
     */
    public static File getChildFile(String rootPath, String relativePath, String fileName) throws IOException {
        File root = new File(rootPath).getCanonicalFile();
        File directory = StringUtils.isBlank(relativePath) ? root : new File(root, relativePath).getCanonicalFile();
        File file = new File(directory, fileName).getCanonicalFile();
        if (!isSubPath(root, directory) || !isSubPath(root, file)) {
            throw new FilePathIllegalException(file.getPath());
        }
        validateFilePathAllowed(root, directory);
        validateFilePathAllowed(root, file);
        return file;
    }

    /**
     * 在指定根目录下解析子路径，并校验最终真实路径仍在根目录内。
     */
    public static File getChildPath(String rootPath, String childPath) throws IOException {
        File root = new File(rootPath).getCanonicalFile();
        File file = new File(root, StringUtils.defaultString(childPath)).getCanonicalFile();
        if (!isSubPath(root, file)) {
            throw new FilePathIllegalException(file.getPath());
        }
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
     * 以rootPath作为安全边界解析文件路径；相对路径按rootPath解析，绝对路径也必须仍在rootPath内。
     */
    public static File getPath(String path, String rootPath) throws IOException {
        if (StringUtils.isBlank(path)) {
            throw new FilePathIllegalException(path);
        }
        File root = new File(rootPath).getCanonicalFile();
        File file = new File(decode(path));
        if (!file.isAbsolute()) {
            file = new File(root, file.getPath());
        }
        file = file.getCanonicalFile();
        // 以rootPath作为文件接口安全边界，避免../../和软链接逃逸到宿主机其他目录。
        if (!isSubPath(root, file)) {
            throw new FilePathIllegalException(file.getPath());
        }
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
     * 获取已存在的合法路径，文件或目录均可，但最终真实路径必须在rootPath内。
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
        File root = new File(rootPath).getCanonicalFile();
        File canonicalDirectory = directory.getCanonicalFile();
        File file = new File(canonicalDirectory, fileName).getCanonicalFile();
        if (!isSubPath(root, canonicalDirectory)
                || !isSubPath(root, file)
                || !canonicalDirectory.getCanonicalPath().equals(file.getParentFile().getCanonicalPath())) {
            throw new FilePathIllegalException(file.getPath());
        }
        validateFilePathAllowed(root, canonicalDirectory);
        validateFilePathAllowed(root, file);
        return file;
    }

    /**
     * 递归校验目录内文件是否落入禁止访问路径，避免打包下载绕过单文件路径限制。
     */
    public static void validateNoForbiddenPathInDirectory(File root, File directory) throws IOException {
        if (directory == null) {
            return;
        }
        File canonicalRoot = root.getCanonicalFile();
        File canonicalFile = directory.getCanonicalFile();
        if (!isSubPath(canonicalRoot, canonicalFile)) {
            throw new FilePathIllegalException(canonicalFile.getPath());
        }
        validateFilePathAllowed(canonicalRoot, canonicalFile);
        if (!canonicalFile.isDirectory()) {
            return;
        }
        File[] childList = canonicalFile.listFiles();
        if (childList == null) {
            return;
        }
        for (File child : childList) {
            validateNoForbiddenPathInDirectory(canonicalRoot, child);
        }
    }

    /**
     * 校验本地文件是否落入file.safe.forbidden.path配置的禁止访问路径。
     */
    public static void validateFilePathAllowed(File root, File file) throws IOException {
        if (root == null || file == null) {
            return;
        }
        File canonicalRoot = root.getCanonicalFile();
        File canonicalFile = file.getCanonicalFile();
        for (File forbiddenPath : getForbiddenPathList(canonicalRoot)) {
            if (isSubPath(forbiddenPath, canonicalFile)) {
                throw new FilePathIllegalException(canonicalFile.getPath());
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
        // 供跨runner转发的虚拟资源路径使用：统一拒绝../和协议/盘符路径，真实文件边界仍由接收端做canonical校验。
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
        if (StringUtils.isBlank(path) || path.contains("../") || path.endsWith("/..")) {
            throw new FilePathIllegalException(filePath);
        }
        if (path.startsWith("jar:file:")) {
            int separatorIndex = path.indexOf("!/");
            if (separatorIndex == -1) {
                throw new FilePathIllegalException(filePath);
            }
            String jarPath = path.substring("jar:file:".length(), separatorIndex);
            String entryPath = path.substring(separatorIndex + 2);
            try {
                File jarRoot = new File(jarRootPath).getCanonicalFile();
                File jarFile = new File(jarPath).getCanonicalFile();
                String jarFileName = jarFile.getName();
                // 仅允许访问指定目录下的白名单Jar资源，避免通过jar:file读取宿主机任意Jar内容。
                if (!jarFile.isFile()
                        || !StringUtils.endsWith(jarFileName, jarSuffix)
                        || !isAllowedFileNamePrefix(jarFileName, allowedJarNamePrefixList)
                        || !isSubPath(jarRoot, jarFile)
                        || !isSafeClasspathResourcePath(entryPath, classpathRootPrefix)) {
                    throw new FilePathIllegalException(filePath);
                }
                return "jar:file:" + jarFile.getPath().replace("\\", "/") + "!/" + entryPath.replace("\\", "/");
            } catch (IOException ex) {
                logger.error("validate jar resource path failed, path: {}", filePath, ex);
                throw new FilePathIllegalException(filePath);
            }
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
        return StringUtils.isNotBlank(normalizedRootPrefix)
                && normalizedPath.startsWith(normalizedRootPrefix)
                && !normalizedPath.contains("../")
                && !normalizedPath.endsWith("/..")
                && !normalizedPath.startsWith("/")
                && !normalizedPath.contains(":");
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
        String pathConfig = Config.getProperties().getProperty(FORBIDDEN_PATH_CONFIG_KEY, StringUtils.EMPTY);
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
            pathList.add(forbiddenPath.getCanonicalFile());
        }
        return pathList;
    }

    /**
     * 判断file的真实路径是否位于root真实路径下。
     */
    public static boolean isSubPath(File root, File file) throws IOException {
        String rootPath = root.getCanonicalPath();
        String filePath = file.getCanonicalPath();
        return filePath.equals(rootPath) || filePath.startsWith(rootPath + File.separator);
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

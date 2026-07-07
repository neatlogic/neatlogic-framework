package neatlogic.framework.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FileSafeUtil {
    private FileSafeUtil() {
    }

    /**
     * 从上传文件名中提取真实文件名，去掉客户端可能带上的目录部分。
     */
    public static String getUploadFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("上传文件名不能为空");
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
            throw new IllegalArgumentException("文件路径不合法");
        }
        return file;
    }

    /**
     * 在指定根目录下解析子路径，并校验最终真实路径仍在根目录内。
     */
    public static File getChildPath(String rootPath, String childPath) throws IOException {
        File root = new File(rootPath).getCanonicalFile();
        File file = new File(root, StringUtils.defaultString(childPath)).getCanonicalFile();
        if (!isSubPath(root, file)) {
            throw new IllegalArgumentException("文件路径不合法");
        }
        return file;
    }

    /**
     * 获取可下载文件，要求路径合法、文件存在且不是目录。
     */
    public static File getDownloadFile(String path, String rootPath) throws IOException {
        File file = getPath(path, rootPath);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("文件不存在，请检查路径！");
        }
        return file;
    }

    /**
     * 以rootPath作为安全边界解析文件路径；相对路径按rootPath解析，绝对路径也必须仍在rootPath内。
     */
    public static File getPath(String path, String rootPath) throws IOException {
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        File root = new File(rootPath).getCanonicalFile();
        File file = new File(decode(path));
        if (!file.isAbsolute()) {
            file = new File(root, file.getPath());
        }
        file = file.getCanonicalFile();
        // 以rootPath作为文件接口安全边界，避免../../和软链接逃逸到宿主机其他目录。
        if (!isSubPath(root, file)) {
            throw new IllegalArgumentException("文件路径不合法");
        }
        return file;
    }

    /**
     * 获取已存在的合法目录，适用于保存目录、打包目录等目录型入参。
     */
    public static File getValidatedDirectory(String path, String rootPath) throws IOException {
        File directory = getPath(path, rootPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("保存路径不存在");
        }
        return directory;
    }

    /**
     * 获取已存在的合法路径，文件或目录均可，但最终真实路径必须在rootPath内。
     */
    public static File getValidatedPath(String path, String rootPath) throws IOException {
        File file = getPath(path, rootPath);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在");
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
            throw new IllegalArgumentException("文件路径不合法");
        }
        return file;
    }

    /**
     * 清洗跨runner、虚拟资源等非本机真实文件路径，返回不含前导斜杠的安全相对路径。
     */
    public static String getSafeRelativePath(String path) {
        String normalizedPath;
        try {
            normalizedPath = decode(path).replace("\\", "/");
        } catch (IOException ex) {
            throw new IllegalArgumentException("文件路径不合法", ex);
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
                throw new IllegalArgumentException("文件路径不合法");
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
            throw new IllegalArgumentException("文件路径不合法", ex);
        }
        if (StringUtils.isBlank(path) || path.contains("../") || path.endsWith("/..")) {
            throw new IllegalArgumentException("文件路径不合法");
        }
        if (path.startsWith("jar:file:")) {
            int separatorIndex = path.indexOf("!/");
            if (separatorIndex == -1) {
                throw new IllegalArgumentException("文件路径不合法");
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
                    throw new IllegalArgumentException("文件路径不合法");
                }
                return "jar:file:" + jarFile.getPath().replace("\\", "/") + "!/" + entryPath.replace("\\", "/");
            } catch (IOException ex) {
                throw new IllegalArgumentException("文件路径不合法", ex);
            }
        }
        if (!isSafeClasspathResourcePath(path, classpathRootPrefix)) {
            throw new IllegalArgumentException("文件路径不合法");
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

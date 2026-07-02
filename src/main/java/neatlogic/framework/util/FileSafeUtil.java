package neatlogic.framework.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class FileSafeUtil {
    private FileSafeUtil() {
    }

    public static String getUploadFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("上传文件名不能为空");
        }
        String normalizedFileName = fileName.replace("\\", "/");
        return new File(normalizedFileName).getName();
    }

    public static File getChildFile(String rootPath, String relativePath, String fileName) throws IOException {
        File root = new File(rootPath).getCanonicalFile();
        File directory = StringUtils.isBlank(relativePath) ? root : new File(root, relativePath).getCanonicalFile();
        File file = new File(directory, fileName).getCanonicalFile();
        if (!isSubPath(root, directory) || !isSubPath(root, file)) {
            throw new IllegalArgumentException("文件路径不合法");
        }
        return file;
    }

    public static File getChildPath(String rootPath, String childPath) throws IOException {
        File root = new File(rootPath).getCanonicalFile();
        File file = new File(root, StringUtils.defaultString(childPath)).getCanonicalFile();
        if (!isSubPath(root, file)) {
            throw new IllegalArgumentException("文件路径不合法");
        }
        return file;
    }

    public static File getDownloadFile(String path, String rootPath) throws IOException {
        File file = getPath(path, rootPath);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("文件不存在，请检查路径！");
        }
        return file;
    }

    public static File getPath(String path, String rootPath) throws IOException {
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        File root = new File(rootPath).getCanonicalFile();
        File file = new File(decode(path)).getCanonicalFile();
        if (!isSubPath(root, file)) {
            throw new IllegalArgumentException("文件路径不合法");
        }
        return file;
    }

    public static File getValidatedDirectory(String path, String rootPath) throws IOException {
        File directory = getPath(path, rootPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("保存路径不存在");
        }
        return directory;
    }

    public static File getValidatedPath(String path, String rootPath) throws IOException {
        File file = getPath(path, rootPath);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在");
        }
        return file;
    }

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

    public static boolean isSubPath(File root, File file) throws IOException {
        String rootPath = root.getCanonicalPath();
        String filePath = file.getCanonicalPath();
        return filePath.equals(rootPath) || filePath.startsWith(rootPath + File.separator);
    }

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

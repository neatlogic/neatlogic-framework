package codedriver.framework.util;

import codedriver.framework.file.core.MinioFileSystemHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class LargeTextUtil {
	private static Logger logger = LoggerFactory.getLogger(LargeTextUtil.class);

	private static MinioFileSystemHandler minioFileSystemHandler;

	@Autowired
	public void setMinioManager(MinioFileSystemHandler _minioFileSystemHandler) {

	}
//
//	private static FileSystem fileSystem;

//	@Autowired
//	public void setFileSystem(FileSystem _fileSystem) {
//		fileSystem = _fileSystem;
//	}
//	
//	public static String writeContent(String filePath, Long id, Object fileName, String content) {
//		content = content == null ? "" : content;
//		String taskID = String.format("%09d", id);
//		for (int i = 0; i < 9; i += 3) {
//			filePath = filePath + File.separator + taskID.substring(i, i + 3);
//		}
//		File dirFile = new File(filePath);
//		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
//			dirFile.mkdirs();
//		}
//
//		boolean fileIsExists = false;
//		String contentFilePath = filePath + File.separator + fileName;
//		File desFile = new File(contentFilePath);
//		if ((!desFile.isFile()) || (!desFile.exists())) {
//			try {
//				fileIsExists = desFile.createNewFile();
//			} catch (IOException e) {
//				logger.error("create task file error : " + e.getMessage() + contentFilePath, e);
//			}
//		} else {
//			fileIsExists = true;
//		}
//		if (fileIsExists) {
//			// FileWriter fw;
//			OutputStreamWriter fw;
//			try {
//				OutputStream fis = new FileOutputStream(desFile);
//				fw = new OutputStreamWriter(fis, "UTF-8");
//				fw.write(content);
//				fw.close();
//				fis.close();
//			} catch (IOException e) {
//				logger.error("write task file error : " + e.getMessage(), e);
//			}
//
//		}
//		return desFile.getAbsolutePath();
//	}
//
	public static String readContent(String filePath, Long start, Integer length) {
		String content = null;
		Reader reader = null;
		InputStream stream = null;
		if (StringUtils.isNotBlank(filePath) && length > 0) {
			try {
				stream = minioFileSystemHandler.getData(filePath);
				reader = new InputStreamReader(stream, "utf-8");
				int index = 0;
				char[] buffer = new char[length];
				if (start > 0) {
					reader.skip(start);
				}
				if ((index = reader.read(buffer)) > 0) {
					content = new String(buffer, 0, index);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException ex) {
						logger.error(ex.getMessage(), ex);
					}
				}
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException ex) {
						logger.error(ex.getMessage(), ex);
					}
				}
			}
		}
		return content;
	}

	public static void readContent(String filePath, OutputStream out) {
		if (StringUtils.isNotBlank(filePath)) {
			InputStream stream = null;
			try {
				stream = minioFileSystemHandler.getData(filePath);
				IOUtils.copy(stream, out);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						logger.error("FileInputStream close error : " + e.getMessage(), e);
					}
				}
			}
		}
	}

	public static String readContent(String filePath) {
		if (StringUtils.isNotBlank(filePath)) {
			String result = "";
			InputStream stream = null;
			try {
				stream = minioFileSystemHandler.getData(filePath);
				result = IOUtils.toString(stream, "utf-8");
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			return result;
		}
		return null;
	}

}

package codedriver.framework.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.restful.logger.ApiAuditAppender;
import codedriver.framework.restful.logger.Content;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class FileUtil {
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 
	 * @Author: baron 2014-1-16
	 * @Description: 写文件内容
	 */
	public static String writeContent(String filePath, Long id, Object fileName, String content) {
		content = content == null ? "" : content;
		String taskID = String.format("%09d", id);
		for (int i = 0; i < 9; i += 3) {
			filePath = filePath + File.separator + taskID.substring(i, i + 3);
		}
		File dirFile = new File(filePath);
		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			dirFile.mkdirs();
		}

		boolean fileIsExists = false;
		String contentFilePath = filePath + File.separator + fileName;
		File desFile = new File(contentFilePath);
		if ((!desFile.isFile()) || (!desFile.exists())) {
			try {
				fileIsExists = desFile.createNewFile();
			} catch (IOException e) {
				logger.error("create task file error : " + e.getMessage() + contentFilePath, e);
			}
		} else {
			fileIsExists = true;
		}
		if (fileIsExists) {
			// FileWriter fw;
			OutputStreamWriter fw;
			try {
				OutputStream fis = new FileOutputStream(desFile);
				fw = new OutputStreamWriter(fis, "UTF-8");
				fw.write(content);
				fw.close();
				fis.close();
			} catch (IOException e) {
				logger.error("write task file error : " + e.getMessage(), e);
			}

		}
		return desFile.getAbsolutePath();
	}

	public static String writeContent(String filePath, String fileName, String content) {
		content = content == null ? "" : content;
		boolean fileIsExists = false;
		File dirFile = new File(filePath);
		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			dirFile.mkdirs();
		}
		String contentFilePath = filePath;
		if (filePath.endsWith(File.separator)) {
			contentFilePath = contentFilePath + fileName;
		} else {
			contentFilePath = contentFilePath + File.separator + fileName;
		}
		File desFile = new File(contentFilePath);
		if ((!desFile.isFile()) || (!desFile.exists())) {
			try {
				fileIsExists = desFile.createNewFile();
			} catch (IOException e) {
				logger.error("create task file error : " + e.getMessage() + contentFilePath, e);
				return "";
			}
		} else {
			fileIsExists = true;
		}
		if (fileIsExists) {
			OutputStreamWriter fw;
			try {
				OutputStream fis = new FileOutputStream(desFile);
				fw = new OutputStreamWriter(fis, "UTF-8");
				fw.write(content);
				fw.close();
				fis.close();
			} catch (IOException e) {
				logger.error("write task file error : " + e.getMessage(), e);
				return "";
			}
		}
		return desFile.getAbsolutePath();
	}

	public static String writeContent(String filePath, String content) {
		return writeContent(filePath, content, false);
	}

	public static String writeContent(String filePath, String content, Boolean isAppend) {
		content = content == null ? "" : content;
		boolean fileIsExists = false;
		File desFile = new File(filePath);
		if ((!desFile.isFile()) || (!desFile.exists())) {
			try {
				File dirFile = desFile.getParentFile();
				if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
					dirFile.mkdirs();
				}

				fileIsExists = desFile.createNewFile();
			} catch (IOException e) {
				logger.error("create task file error : " + e.getMessage() + filePath, e);
			}
		} else {
			fileIsExists = true;
		}
		if (fileIsExists) {
			OutputStreamWriter fw;
			try {
				OutputStream fis = new FileOutputStream(desFile, isAppend);
				fw = new OutputStreamWriter(fis, "UTF-8");
				fw.write(content);
				fw.close();
				fis.close();
			} catch (IOException e) {
				logger.error("write task file error : " + e.getMessage(), e);
			}
		}
		return desFile.getAbsolutePath();
	}

	public static String writeContent(Content apiAuditContent, Boolean isAppend, ApiAuditAppender appender) {		
		String filePath = appender.getFilePath();
		filePath = createNewFile(filePath);
		if (filePath == null) {
			return null;		
		}
		try {
			File desFile = new File(filePath);
			String content = apiAuditContent.format();
			content = content == null ? "" : content;
			writeContent(content, desFile, isAppend);
			long length = desFile.length();
			if(length < appender.getMaxFileSize()) {
				return filePath;				
			}
			SimpleDateFormat sdf = new SimpleDateFormat(appender.getFileNamepattern());
			String newFilePath = filePath + sdf.format(new Date());
			newFilePath = createNewFile(newFilePath);
			if(newFilePath == null) {
				return filePath;
			}
			File newFile = new File(newFilePath);
			boolean flag = copy(desFile, newFile);
			if(!flag) {
				newFile.delete();
			}
			writeContent("", desFile, false);
			int maxHistory = appender.getMaxHistory();
			if(maxHistory < 0) {
				return filePath;
			}
			int index = filePath.lastIndexOf(File.separator);
			String dirPath = filePath.substring(0, index);		
			File[] fileList = new File(dirPath).listFiles();
			int deleteCount = fileList.length - (maxHistory + 1);
			if(deleteCount < 1) {
				return filePath;
			}
			Map<Long, File> fileMap = new HashMap<>();
			List<Long> lastModifiedList = new ArrayList<>();
			for(File file : fileList) {
				fileMap.put(file.lastModified(), file);
				lastModifiedList.add(file.lastModified());
			}
			lastModifiedList.sort(new Comparator<Long>() {
				@Override
				public int compare(Long o1, Long o2) {
					return o1.compareTo(o2);
				}});
			for(int i = 0; i < deleteCount; i++) {
				fileMap.get(lastModifiedList.get(i)).delete();
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}	
		return filePath;
	}
	
	private static String createNewFile(String filePath) {
		File file = new File(filePath);
		if ((!file.isFile()) || (!file.exists())) {
			try {
				File dirFile = file.getParentFile();
				if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
					dirFile.mkdirs();
				}

				boolean fileIsExists = file.createNewFile();
				if(fileIsExists) {
					return file.getAbsolutePath();
				}
				return null;
			} catch (IOException e) {
				logger.error("create task file error : " + e.getMessage() + filePath, e);
			}
		}
		return file.getAbsolutePath();
	}
	
	private static boolean copy(File oldFile, File newFile) {
		try(	FileInputStream fis = new FileInputStream(oldFile);
				InputStreamReader fir = new InputStreamReader(fis, "UTF-8");
				OutputStream fos = new FileOutputStream(newFile, true);
				OutputStreamWriter fow = new OutputStreamWriter(fos, "UTF-8");){
			char[] cbuf = new char[1024];
			while(fir.read(cbuf) != -1) {
				fow.write(cbuf);
				fow.flush();
			}		
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			return false;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	private static void writeContent(String content, File file, boolean isAppend) {
		try (	OutputStream fos = new FileOutputStream(file, isAppend);
				OutputStreamWriter fow = new OutputStreamWriter(fos, "UTF-8");
				) {
			fow.write(content);
			fow.flush();
		} catch (IOException e) {
			logger.error("write task file error : " + e.getMessage(), e);
		}
	}
	public static List<String> readContentToList(String filePath, String fileName) {
		FileReader fr = null;
		BufferedReader filebr = null;
		String separator = System.getProperty("file.separator");
		List<String> returnList = new ArrayList<String>();
		try {
			if (filePath == null || "".equals(filePath) || fileName == null || "".equals(fileName)) {
				return null;
			} else {
				if (filePath.endsWith(separator)) {
					filePath = filePath + fileName;
				} else {
					filePath = filePath + separator + fileName;
				}
			}
			File desFile = new File(filePath);
			if (desFile.isFile() && desFile.exists()) {
				fr = new FileReader(desFile);
				filebr = new BufferedReader(fr);
				String inLine = "";
				while ((inLine = filebr.readLine()) != null) {
					if (!inLine.equals("")) {
						returnList.add(inLine);
					}
				}
			} else {
				returnList.add("出错，找不到对应的内容文件！");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}

			if (filebr != null) {
				try {
					filebr.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return returnList;
	}

	public static String readContent(String filePath, String fileName) {
		String separator = File.separator;
		if (filePath == null || "".equals(filePath) || fileName == null || "".equals(fileName)) {
			return null;
		} else {
			if (filePath.endsWith(separator)) {
				filePath = filePath + fileName;
			} else {
				filePath = filePath + separator + fileName;
			}
			return readContent(filePath);
		}
	}

	public static String readContent(String filePath) {
		if (StringUtils.isBlank(filePath)) {
			return null;
		} else {
			String result = "";
			FileInputStream fr = null;
			File desFile = new File(filePath);
			try {
				if (desFile.isFile() && desFile.exists()) {
					fr = new FileInputStream(desFile);
					result = IOUtils.toString(fr, "utf-8");
				} else {
					result = "出错，找不到对应的内容文件！";
				}
			} catch (Exception ex) {
				result = ex.getMessage();
				logger.error(ex.getMessage(), ex);
			} finally {
				if (fr != null) {
					try {
						fr.close();
					} catch (IOException e) {
						logger.error("FileInputStream close error : " + e.getMessage(), e);
					}
				}
			}
			return result;
		}
	}

	public static String readContent(InputStream inputstream) {
		String result = "";
		try {
			result = IOUtils.toString(inputstream, "utf-8");
		} catch (Exception ex) {
			result = ex.getMessage();
			logger.error(ex.getMessage(), ex);
		} finally {
			if (inputstream != null) {
				try {
					inputstream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @Author: baron 2014-1-16
	 * @Description: 读文件内容
	 */
	public static String readContent(String filePath, Long id, String fileName) {
		String taskID = String.format("%09d", id);
		FileInputStream fr = null;
		String result = "";
		try {
			for (int i = 0; i < 9; i += 3) {
				filePath = filePath + "/" + taskID.substring(i, i + 3);
			}
			File desFile = new File(filePath + "/" + fileName);
			if (desFile.isFile() && desFile.exists()) {
				fr = new FileInputStream(desFile);
				result = IOUtils.toString(fr, "utf-8");
			} else {
				result = "出错，找不到对应的内容文件！";
			}
		} catch (FileNotFoundException e) {
			result = e.getMessage();
			logger.error("read task file not found error : " + e.getMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			result = e.getMessage();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					logger.error("FileInputStream close error : " + e.getMessage(), e);
				}
			}
		}
		return result;
	}

	/**
	 * @Author: chenqiwei
	 * @Time:Apr 1, 2019
	 * @Description: TODO
	 * @param @param
	 *            filePath
	 * @param @param
	 *            start
	 * @param @param
	 *            length
	 * @param @return
	 * @return String
	 */
	public static String readContent(String filePath, Long start, Integer length) {
		String content = null;
		Reader reader = null;
		InputStream stream = null;
		if (filePath != null && !"".equals(filePath) && length > 0) {
			filePath = filePath.replace(" ", "").replace("%", "").replace("..", "");
			File desFile = new File(filePath);
			try {
				if (desFile.isFile() && desFile.exists()) {
					stream = new FileInputStream(desFile);
					reader = new InputStreamReader(stream, "utf-8");

					int index = 0;
					char[] buffer = new char[length];
					if (start > 0) {
						reader.skip(start);
					}
					if ((index = reader.read(buffer)) > 0) {
						content = new String(buffer, 0, index);
					}
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
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

	/**
	 * 根据文件路径删除文件
	 */
	public static void deleteContent(String filePath) {
		File desFile = new File(filePath);
		if (desFile.exists()) {
			desFile.delete();
		}
	}

	/**
	 * @Description: 向压缩文件(zip)内写入内容
	 * @Author:fandong
	 * @param zipFilePath
	 *            压缩包路径 like c:\ZipTest\test4.zip
	 * @param subPath
	 *            压缩包内路径 like 11/22/33/aa.txt
	 * @param fis
	 *            inputstream ,please close fis when write complete
	 * @return: void
	 * @Date 16:03 2019/4/16
	 */

	public synchronized static String writeZipContent(String zipFilePath, String subPath, InputStream fis) throws Exception {
		if (StringUtils.isNotBlank(zipFilePath) && StringUtils.isNotBlank(subPath) && fis != null) {
			FileLock fileLock = null;
			FileChannel channel = null;
			RandomAccessFile randomAccessFile = null;
			try {
				File lockFile = new File(zipFilePath + ".lock");
				if (!lockFile.getParentFile().exists()) {
					lockFile.getParentFile().mkdirs();
				}
				randomAccessFile = new RandomAccessFile(lockFile, "rws");
				channel = randomAccessFile.getChannel();
				fileLock = channel.lock();
				if (fileLock != null && fileLock.isValid()) {
					ZipFile zipFile = new ZipFile(zipFilePath);
					ZipParameters parameters = new ZipParameters();
					parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
					parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
					parameters.setSourceExternalStream(true);

					parameters.setFileNameInZip(subPath);

					File zip = new File(zipFilePath);
					if (zip.exists()) {
						FileHeader fileHeader = zipFile.getFileHeader(subPath);
						if (fileHeader != null) {
							zipFile.removeFile(fileHeader.getFileName());
						}
					}
					zipFile.addStream(fis, parameters);
					return zip.getAbsolutePath() + "||" + subPath;
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				throw ex;
			} finally {
				if (fileLock != null) {
					fileLock.release();
					fileLock = null;
				}
				if (channel != null) {
					channel.close();
					channel = null;
				}
				if (randomAccessFile != null) {
					randomAccessFile.close();
					randomAccessFile = null;
				}
			}
		}
		return "";
	}

	/**
	 * @Description: 向压缩文件(zip)内写入多条内容
	 * @Author:fandong
	 * @param zipFilePath
	 * @param subPathList
	 *            path list
	 * @param dataList
	 *            string list
	 * @return: void
	 * @throws Exception
	 * @Date 19:07 2019/4/17
	 */
	public synchronized static void writeZipContentWithDataList(String zipFilePath, List<String> subPathList, List<String> dataList) throws Exception {
		FileLock fileLock = null;
		FileChannel channel = null;
		RandomAccessFile randomAccessFile = null;
		try {
			File lockFile = new File(zipFilePath + ".lock");
			if (!lockFile.getParentFile().exists()) {
				lockFile.getParentFile().mkdirs();
			}
			randomAccessFile = new RandomAccessFile(lockFile, "rws");
			channel = randomAccessFile.getChannel();
			fileLock = channel.lock();
			if (fileLock != null && fileLock.isValid()) {
				ZipFile zipFile = new ZipFile(zipFilePath);
				ZipParameters parameters = new ZipParameters();
				parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
				parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
				parameters.setSourceExternalStream(true);
				for (int i = 0; i < subPathList.size(); i++) {
					parameters.setFileNameInZip(subPathList.get(i));
					File zip = new File(zipFilePath);
					if (zip.exists()) {
						FileHeader fileHeader = zipFile.getFileHeader(subPathList.get(i));
						if (fileHeader != null) {
							zipFile.removeFile(fileHeader.getFileName());
						}
					}
					try (InputStream fis = IOUtils.toInputStream(dataList.get(i), StandardCharsets.UTF_8)) {
						zipFile.addStream(fis, parameters);
					}
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (fileLock != null) {
				fileLock.release();
				fileLock = null;
			}
			if (channel != null) {
				channel.close();
				channel = null;
			}
			if (randomAccessFile != null) {
				randomAccessFile.close();
				randomAccessFile = null;
			}
		}
	}

	abstract static class Thandler implements Runnable {
		private int i;

		public Thandler(int _i) {
			i = _i;
		}

		public int getI() {
			return i;
		}
	}

	public static String writeZipContent(String zipFilePath, String subPath, String data) throws Exception {
		try (InputStream fis = IOUtils.toInputStream(data, StandardCharsets.UTF_8)) {
			return writeZipContent(zipFilePath, subPath, fis);
		}
	}

	/**
	 * @Description:从压缩文件中读取内容，不解压文件
	 * @Author:fandong
	 * @param zipFilePath
	 *            like c:\ZipTest\test4.zip
	 * @param subPath
	 *            like 11/22/33/aa.txt
	 * @return: java.lang.String 读取出来的内容
	 * @Date 16:07 2019/4/16
	 */
	public synchronized static String readZipContent(String zipFilePath, String subPath) {
		String readData = "";
		File lockFile = new File(zipFilePath + ".lock");
		if (!lockFile.getParentFile().exists()) {
			lockFile.getParentFile().mkdirs();
		}
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
			 FileChannel channel = randomAccessFile.getChannel()) {

			FileLock fileLock = channel.lock();
			if (fileLock != null && fileLock.isValid()) {
				File file = new File(zipFilePath);
				if (!file.exists()) {
					readData =  "出错，找不到压缩文件";
				}
				ZipFile zipFile = new ZipFile(zipFilePath);
				FileHeader fileHeader = zipFile.getFileHeader(subPath);
				if(fileHeader != null){
					ZipInputStream is = zipFile.getInputStream(fileHeader);
					StringWriter writer = new StringWriter();
					IOUtils.copy(is, writer, StandardCharsets.UTF_8.name());
					is.close();
					readData =  writer.toString();
				}else{
					readData = "出错：压缩文件内未发现该日志文件";
				}
			}



		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			readData =  "出错，压缩文件内容读取失败";
		}

		return readData;
	}

//	public static void main(String[] args) throws Exception {
//		//并发嵌套测试，（写与读） 10 threads
//		new Thread(
//				new Runnable() {
//					@Override
//					public void run() {
//						for(int i = 0;i<10;i++){
//							int finalI = i;
//							new Thread(
//									new Runnable() {
//										@Override
//										public void run() {
//											List<String> pathList = new ArrayList<>();
//											pathList.add(finalI*100 + finalI + File.separator + "param");
//											pathList.add(finalI*100 + finalI + File.separator + "result");
//											List<String> dataList = new ArrayList<>();
//											dataList.add("paramparamparamparamparamparamparamparamparamparamparamparam");
//											dataList.add("resultresultresultresultresultresultresultresultresultresult");
//											Thread.currentThread().setName("write " + finalI);
//											try {
//												System.out.println("write start" +  finalI);
//												writeZipContentWithDataList("C:\\app\\data\\balantflow\\rest\\audit\\server_1_restlog_12.zip",pathList, dataList);
//												System.out.println("write end" +  finalI);
//											} catch (Exception e) {
//												e.printStackTrace();
//											}
//
//										}
//									}
//
//							).start();
//						}
//					}
//				}
//		).start();
//
//
//		new Thread(
//				new Runnable() {
//
//					@Override
//					public void run() {
//						for(int i = 0;i<10;i++){
//							int finalI = i;
//							new Thread(
//									new Runnable() {
//										@Override
//										public void run() {
//											Thread.currentThread().setName("read " + finalI);
//											System.out.println(Thread.currentThread().getName() + readZipContent("C:\\app\\data\\balantflow\\rest\\audit\\server_1_restlog_12.zip","60133/result"));
//
//										}
//									}
//
//							).start();
//						}
//					}
//				}
//		).start();
//	}

}

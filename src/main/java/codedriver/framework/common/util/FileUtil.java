package codedriver.framework.common.util;

import codedriver.framework.exception.file.FilePathIllegalException;
import codedriver.framework.exception.file.FileStorageMediumHandlerNotFoundException;
import codedriver.framework.file.core.FileStorageMediumFactory;
import codedriver.framework.file.core.IFileStorageHandler;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
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
                fw = new OutputStreamWriter(fis, StandardCharsets.UTF_8);
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
                fw = new OutputStreamWriter(fis, StandardCharsets.UTF_8);
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
                fw = new OutputStreamWriter(fis, StandardCharsets.UTF_8);
                fw.write(content);
                fw.close();
                fis.close();
            } catch (IOException e) {
                logger.error("write task file error : " + e.getMessage(), e);
            }
        }
        return desFile.getAbsolutePath();
    }

    public static List<String> readContentToList(String filePath, String fileName) {
        FileReader fr = null;
        BufferedReader filebr = null;
        String separator = System.getProperty("file.separator");
        List<String> returnList = new ArrayList<String>();
        try {
            if (StringUtils.isBlank(filePath) || StringUtils.isBlank(fileName)) {
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
                String inLine;
                while ((inLine = filebr.readLine()) != null) {
                    if (!inLine.equals("")) {
                        returnList.add(inLine);
                    }
                }
            } else {
                returnList.add("出错，找不到对应的内容文件！");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
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
        if (StringUtils.isBlank(filePath) || StringUtils.isBlank(fileName)) {
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
            String result;
            FileInputStream fr = null;
            File desFile = new File(filePath);
            try {
                if (desFile.isFile() && desFile.exists()) {
                    fr = new FileInputStream(desFile);
                    result = IOUtils.toString(fr, StandardCharsets.UTF_8);
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
        String result;
        try {
            result = IOUtils.toString(inputstream, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            result = ex.getMessage();
            logger.error(ex.getMessage(), ex);
        } finally {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }


    /*
     * @Description:
     * @Author: chenqiwei
     * @Date: 2021/2/22 3:03 下午
     * @Params: [filePath, start, length]
     * @Returns: java.lang.String
     **/
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
                    reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

                    int index;
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

    /*
     * @Description:  向压缩文件(zip)内写入内容
     * @Author: chenqiwei
     * @Date: 2021/2/22 3:04 下午
     * @Params: [zipFilePath:压缩包路径 like c:\ZipTest\test4.zip, subPath:压缩包内路径 like 11/22/33/aa.txt, fis:inputstream ,please close fis when write complete]
     * @Returns: java.lang.String
     **/
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

    /*
     * @Description:  向压缩文件(zip)内写入多条内容
     * @Author: chenqiwei
     * @Date: 2021/2/22 3:04 下午
     * @Params: [zipFilePath, subPathList, dataList]
     * @Returns: void
     **/
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

    /*
     * @Description: 从压缩文件中读取内容，不解压文件
     * @Author: chenqiwei
     * @Date: 2021/2/22 3:05 下午
     * @Params: [zipFilePath:like c:\ZipTest\test4.zip, subPath:like 11/22/33/aa.txt]
     * @Returns: java.lang.String
     **/
    public synchronized static String readZipContent(String zipFilePath, String subPath) {
        String readData = "";
        File lockFile = new File(zipFilePath + ".lock");
        if (!lockFile.getParentFile().exists()) {
            lockFile.getParentFile().mkdirs();
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw"); FileChannel channel = randomAccessFile.getChannel()) {

            FileLock fileLock = channel.lock();
            if (fileLock != null && fileLock.isValid()) {
                File file = new File(zipFilePath);
                if (!file.exists()) {
                    readData = "出错，找不到压缩文件";
                }
                ZipFile zipFile = new ZipFile(zipFilePath);
                FileHeader fileHeader = zipFile.getFileHeader(subPath);
                if (fileHeader != null) {
                    ZipInputStream is = zipFile.getInputStream(fileHeader);
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(is, writer, StandardCharsets.UTF_8.name());
                    is.close();
                    readData = writer.toString();
                } else {
                    readData = "出错：压缩文件内未发现该日志文件";
                }
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            readData = "出错，压缩文件内容读取失败";
        }

        return readData;
    }

    /*
     * @Description: 根据storageMediumHandler获取存储介质Handler，从而上传到对应的存储介质中
     * @Author: chenqiwei
     * @Date: 2021/2/22 2:58 下午
     * @Params: [storageMediumHandler, tenantUuid, inputStream, fileId, contentType, fileType]
     * @Returns: java.lang.String
     **/
    public static String saveData(String storageMediumHandler, String tenantUuid, InputStream inputStream, String fileId, String contentType, String fileType) throws Exception {
        IFileStorageHandler handler = FileStorageMediumFactory.getHandler(storageMediumHandler);
        if (handler == null) {
            throw new FileStorageMediumHandlerNotFoundException(storageMediumHandler);
        }
        return handler.saveData(tenantUuid, inputStream, fileId, contentType, fileType);
    }

    public static InputStream getData(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath) || !filePath.contains(":")) {
            throw new FilePathIllegalException(filePath);
        }
        String prefix = filePath.split(":")[0];
        IFileStorageHandler handler = FileStorageMediumFactory.getHandler(prefix.toUpperCase());
        if (handler == null) {
            throw new FileStorageMediumHandlerNotFoundException(prefix);
        }
        return handler.getData(filePath);
    }

    public static void deleteData(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath) || !filePath.contains(":")) {
            throw new FilePathIllegalException(filePath);
        }
        String prefix = filePath.split(":")[0];
        IFileStorageHandler handler = FileStorageMediumFactory.getHandler(prefix.toUpperCase());
        if (handler == null) {
            throw new FileStorageMediumHandlerNotFoundException(prefix);
        }
        handler.deleteData(filePath);
    }

    public static long getDataLength(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath) || !filePath.contains(":")) {
            throw new FilePathIllegalException(filePath);
        }
        String prefix = filePath.split(":")[0];
        IFileStorageHandler handler = FileStorageMediumFactory.getHandler(prefix.toUpperCase());
        if (handler == null) {
            throw new FileStorageMediumHandlerNotFoundException(prefix);
        }
        return handler.getDataLength(filePath);
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

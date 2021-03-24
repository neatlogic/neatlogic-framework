package codedriver.framework.file.core;

import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.exception.file.FilePathIllegalException;
import codedriver.framework.exception.file.FileStorageMediumHandlerNotFoundException;
import io.minio.MinioClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RootComponent
public class MinioFileSystemHandler implements InitializingBean, IFileStorageHandler {

	public static final String NAME = "MINIO";

	private MinioClient minioClient;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
		if(StringUtils.isNotBlank(Config.MINIO_URL())) {
			this.minioClient = new MinioClient(Config.MINIO_URL(), Config.MINIO_ACCESSKEY(), Config.MINIO_SECRETKEY());
			minioClient.setTimeout(TimeUnit.SECONDS.toMillis(10),0,0);
		}
	}

	/**
	 * 
	 * @param tenantUuid
	 * @param inputStream
	 * @param contentType
	 * @return
	 */
	@Override
	public String saveData(String tenantUuid, InputStream inputStream, String fileId, String contentType, String fileType) throws Exception {
		if(minioClient == null){
			throw new FileStorageMediumHandlerNotFoundException("minio");
		}
		// 检查存储桶是否已经存在
		boolean bucketExists = minioClient.bucketExists(Config.MINIO_BUCKET());
		if (!bucketExists) {
			// 创建一个名为bucketName的存储桶，用于存储照片等zip文件。
			minioClient.makeBucket(Config.MINIO_BUCKET());
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		String finalPath = "/" + tenantUuid + "/upload/" + fileType + "/" + format.format(new Date()) + "/" + fileId;
		// 使用putObject上传一个文件到存储桶中
		minioClient.putObject(Config.MINIO_BUCKET(), finalPath, inputStream, contentType);
//		fileVo.setPath("minio:" + finalPath);
		return MinioFileSystemHandler.NAME.toLowerCase() + ":" + finalPath;
	}

	/**
	 * 删除
	 * 
	 * @throws Exception
	 */
	public void deleteData(String filePath) throws Exception {
		if(minioClient == null){
			throw new FileStorageMediumHandlerNotFoundException("minio");
		}
		if (StringUtils.isNotBlank(filePath) && filePath.startsWith(NAME.toLowerCase() + ":")) {
			String path = filePath.replaceAll(NAME.toLowerCase() + ":", "");
			minioClient.removeObject(Config.MINIO_BUCKET(), path);
		} else {
			throw new FilePathIllegalException(filePath);
		}

	}

	/**
	 * 获取
	 *
	 * @param path
	 * @return
	 * @throws Exception
	 */
	@Override
	public InputStream getData(String path) throws Exception {
		if(minioClient == null){
			throw new FileStorageMediumHandlerNotFoundException("minio");
		}
		InputStream in = minioClient.getObject(Config.MINIO_BUCKET(), path.replaceAll(NAME.toLowerCase() + ":", ""));
		return in;
	}

	@Override
	public long getDataLength(String filePath) throws Exception {
		if(minioClient == null){
			throw new FileStorageMediumHandlerNotFoundException("minio");
		}
		long length = 0;
		length = minioClient.statObject(Config.MINIO_BUCKET(), filePath.replaceAll(NAME.toLowerCase() + ":", "")).length();
		return length;
	}
}

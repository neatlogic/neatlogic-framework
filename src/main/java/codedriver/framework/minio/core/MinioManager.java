package codedriver.framework.minio.core;

import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.exception.file.FilePathIllegalException;
import codedriver.framework.file.core.IFileStorageMediumHandler;
import io.minio.MinioClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@RootComponent
public class MinioManager implements InitializingBean, IFileStorageMediumHandler {

	public static final String NAME = "MINIO";

	private MinioClient minioClient;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
		this.minioClient = new MinioClient(Config.MINIO_URL(), Config.MINIO_ACCESSKEY(), Config.MINIO_SECRETKEY());
	}

	/**
	 * 
	 * @param tenantUuid
	 * @param inputStream
	 * @param contentType
	 * @return
	 */
	@Override
	public String saveData(String tenantUuid, InputStream inputStream, Long fileId, String contentType, String fileType) throws Exception {
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
		return MinioManager.NAME.toLowerCase() + ":" + finalPath;
	}

	/**
	 * 删除
	 * 
	 * @throws Exception
	 */
	public void deleteData(String filePath) throws Exception {
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
		InputStream in = minioClient.getObject(Config.MINIO_BUCKET(), path.replaceAll(NAME.toLowerCase() + ":", ""));
		return in;
	}

}

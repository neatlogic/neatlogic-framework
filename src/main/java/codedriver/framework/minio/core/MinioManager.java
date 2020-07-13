package codedriver.framework.minio.core;

import java.io.InputStream;

import org.springframework.beans.factory.InitializingBean;

import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import io.minio.MinioClient;

@RootComponent
public class MinioManager implements InitializingBean {

	private MinioClient minioClient;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
		this.minioClient = new MinioClient(Config.MINIO_URL(), Config.MINIO_ACCESSKEY(), Config.MINIO_SECRETKEY());
	}

	/**
	 * 
	 * @param bucketName  存储桶名
	 * @param in          输入文件流
	 * @param contentType 内容类型
	 * @return
	 */
	public String saveObject(String bucketName, String ObjectName, InputStream in, Long size, String contentType) throws Exception {
		// 检查存储桶是否已经存在
		boolean bucketExists = minioClient.bucketExists(bucketName);
		if (!bucketExists) {
			// 创建一个名为bucketName的存储桶，用于存储照片等zip文件。
			minioClient.makeBucket(bucketName);
		}
		// 使用putObject上传一个文件到存储桶中
		minioClient.putObject(bucketName, ObjectName, in, contentType);
		return minioClient.getObjectUrl(bucketName, ObjectName);
	}

	/**
	 * 删除
	 * 
	 * @param bucketName 存储桶名
	 * @param ObjectName 输入文件流
	 * @throws Exception
	 */
	public void removeObject(String bucketName, String objectName) throws Exception {
		minioClient.removeObject(bucketName, objectName);
	}

	/**
	 * 获取
	 * 
	 * @param bucketName 存储桶名
	 * @param objectName 输入文件流
	 * @return
	 * @throws Exception
	 */
	public InputStream getObject(String bucketName, String objectName) throws Exception {
		InputStream in = minioClient.getObject(bucketName, objectName);
		return in;
	}

}

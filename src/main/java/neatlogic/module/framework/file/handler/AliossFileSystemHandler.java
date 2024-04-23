package neatlogic.module.framework.file.handler;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.ObjectMetadata;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.file.FilePathIllegalException;
import neatlogic.framework.exception.file.FileStorageMediumHandlerNotFoundException;
import neatlogic.framework.file.core.IFileStorageHandler;
import neatlogic.framework.file.dto.FileVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AliossFileSystemHandler implements InitializingBean, IFileStorageHandler {

    public static final String NAME = "ALIOSS";

    private OSSClient ossClient;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(Config.getConfigProperty("alioss.url"))) {
            // 创建ClientConfiguration。ClientConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数。
            ClientConfiguration conf = new ClientConfiguration();
            // 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
            conf.setMaxConnections(1024);
            // 设置Socket层传输数据的超时时间，默认为50000毫秒。
            conf.setSocketTimeout(50000);
            // 设置建立连接的超时时间，默认为50000毫秒。
            conf.setConnectionTimeout(50000);
            // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
            conf.setConnectionRequestTimeout(1000);
            // 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
            conf.setIdleConnectionTime(60000);
            // 设置失败请求重试次数，默认为3次。
            conf.setMaxErrorRetry(5);
            CredentialsProvider credentialsProvider = new DefaultCredentialProvider(Config.getConfigProperty("alioss.accesskey", "aliossadmin"), Config.getConfigProperty("alioss.secretkey", "aliossadmin"));
            this.ossClient = new OSSClient(Config.getConfigProperty("alioss.url"), credentialsProvider, conf);
        }
    }

    /**
     * @param tenantUuid  租户uuid
     * @param inputStream 输入流
     * @param file 文件
     * @return 附件路径
     */
    @Override
    public String saveData(String tenantUuid, InputStream inputStream, FileVo file) throws Exception {
        String bucket = Config.getConfigProperty("alioss.bucket", "neatlogic");
        if (ossClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("alioss");
        }
        // 检查存储桶是否已经存在
        boolean bucketExists = ossClient.doesBucketExist(bucket);
        if (!bucketExists) {
            // 创建一个名为bucketName的存储桶，用于存储照片等zip文件。
            ossClient.createBucket(Config.getConfigProperty("alioss.bucket", "neatlogic"));
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        String finalPath = "/" + tenantUuid + "/upload/" + file.getType() + "/" + format.format(new Date()) + "/" + file.getPathName();
        // 使用putObject上传一个文件到存储桶中
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        ossClient.putObject(bucket, finalPath, inputStream, metadata);
//		fileVo.setPath("minio:" + finalPath);
        return AliossFileSystemHandler.NAME.toLowerCase() + ":" + finalPath;
    }

    @Override
    public InputStream getData(String path) throws Exception {
        if (ossClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("alioss");
        }
        return ossClient.getObject(Config.getConfigProperty("alioss.bucket", "neatlogic"), path.replaceAll(NAME.toLowerCase() + ":", "")).getObjectContent();
    }

    @Override
    public void deleteData(String filePath) throws Exception {
        if (ossClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("alioss");
        }
        if (StringUtils.isNotBlank(filePath) && filePath.startsWith(NAME.toLowerCase() + ":")) {
            String path = filePath.replaceAll(NAME.toLowerCase() + ":", "");
            ossClient.deleteObject(Config.getConfigProperty("alioss.bucket", "neatlogic"), path);
        } else {
            throw new FilePathIllegalException(filePath);
        }
    }

    @Override
    public long getDataLength(String filePath) throws Exception {
        if (ossClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("alioss");
        }
        return ossClient.getObjectMetadata(Config.getConfigProperty("alioss.bucket", "neatlogic"), filePath.replaceAll(NAME.toLowerCase() + ":", "")).getContentLength();
    }

    @Override
    public boolean isExit(String filePath) throws Exception {
        if (ossClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("alioss");
        }
        ossClient.doesObjectExist(Config.getConfigProperty("alioss.bucket", "neatlogic"), filePath.replaceAll(NAME.toLowerCase() + ":", ""));
        return true;
    }


}

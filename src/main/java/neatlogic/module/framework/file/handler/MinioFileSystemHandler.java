/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.file.handler;

import io.minio.MinioClient;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.file.FilePathIllegalException;
import neatlogic.framework.exception.file.FileStorageMediumHandlerNotFoundException;
import neatlogic.framework.file.core.IFileStorageHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
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
        if (StringUtils.isNotBlank(Config.getConfigProperty("minio.url"))) {
            this.minioClient = new MinioClient(Config.getConfigProperty("minio.url"), Config.getConfigProperty("minio.accesskey", "minioadmin"), Config.getConfigProperty("minio.secretkey", "minioadmin"));
            minioClient.setTimeout(TimeUnit.SECONDS.toMillis(10), 0, 0);
        }
    }

    /**
     * @param tenantUuid  租户uuid
     * @param inputStream 输入流
     * @param contentType contentType
     * @return 附件路径
     */
    @Override
    public String saveData(String tenantUuid, InputStream inputStream, String fileId, String contentType, String fileType) throws Exception {
        if (minioClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("minio");
        }
        // 检查存储桶是否已经存在
        boolean bucketExists = minioClient.bucketExists(Config.getConfigProperty("minio.bucket", "neatlogic"));
        if (!bucketExists) {
            // 创建一个名为bucketName的存储桶，用于存储照片等zip文件。
            minioClient.makeBucket(Config.getConfigProperty("minio.bucket", "neatlogic"));
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        String finalPath = "/" + tenantUuid + "/upload/" + fileType + "/" + format.format(new Date()) + "/" + fileId;
        // 使用putObject上传一个文件到存储桶中
        minioClient.putObject(Config.getConfigProperty("minio.bucket", "neatlogic"), finalPath, inputStream, contentType);
//		fileVo.setPath("minio:" + finalPath);
        return MinioFileSystemHandler.NAME.toLowerCase() + ":" + finalPath;
    }

    /**
     * 删除附件
     *
     * @param filePath 附件路径
     */
    public void deleteData(String filePath) throws Exception {
        if (minioClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("minio");
        }
        if (StringUtils.isNotBlank(filePath) && filePath.startsWith(NAME.toLowerCase() + ":")) {
            String path = filePath.replaceAll(NAME.toLowerCase() + ":", "");
            minioClient.removeObject(Config.getConfigProperty("minio.bucket", "neatlogic"), path);
        } else {
            throw new FilePathIllegalException(filePath);
        }

    }

    /**
     * 获取附件输入流
     *
     * @param path 附件路径
     * @return 附件输入流
     */
    @Override
    public InputStream getData(String path) throws Exception {
        if (minioClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("minio");
        }
        return minioClient.getObject(Config.getConfigProperty("minio.bucket", "neatlogic"), path.replaceAll(NAME.toLowerCase() + ":", ""));
    }

    @Override
    public long getDataLength(String filePath) throws Exception {
        if (minioClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("minio");
        }
        return minioClient.statObject(Config.getConfigProperty("minio.bucket", "neatlogic"), filePath.replaceAll(NAME.toLowerCase() + ":", "")).length();
    }

    @Override
    public boolean isExit(String filePath) throws Exception {
        if (minioClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("minio");
        }
        minioClient.statObject(Config.getConfigProperty("minio.bucket", "neatlogic"), filePath.replaceAll(NAME.toLowerCase() + ":", ""));
        return true;
    }
}

/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.file.handler;

import io.minio.MinioClient;
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
     * @param fileParam 文件
     * @return 附件路径
     */
    @Override
    public String saveData(String tenantUuid, InputStream inputStream, FileVo fileParam) throws Exception {
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
        String finalPath = "/" + tenantUuid + "/upload/" + fileParam.getType() + "/" + format.format(new Date()) + "/" + fileParam.getPathName();
        // 使用putObject上传一个文件到存储桶中
        minioClient.putObject(Config.getConfigProperty("minio.bucket", "neatlogic"), finalPath, inputStream, fileParam.getContentType());
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

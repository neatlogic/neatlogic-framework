/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.file.handler;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
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
            this.minioClient =
                    MinioClient.builder()
                            .endpoint(Config.getConfigProperty("minio.url"))
                            .credentials(Config.getConfigProperty("minio.accesskey", "minioadmin"), Config.getConfigProperty("minio.secretkey", "minioadmin"))
                            .build();
            //this.minioClient = new MinioClient(Config.getConfigProperty("minio.url"), Config.getConfigProperty("minio.accesskey", "minioadmin"), Config.getConfigProperty("minio.secretkey", "minioadmin"));
            minioClient.setTimeout(TimeUnit.SECONDS.toMillis(10), 0, 0);
        }
    }

    /**
     * @param tenantUuid  租户uuid
     * @param inputStream 输入流
     * @param fileParam   文件
     * @return 附件路径
     */
    @Override
    public String saveData(String tenantUuid, InputStream inputStream, FileVo fileParam) throws Exception {
//        if (minioClient == null) {
//            throw new FileStorageMediumHandlerNotFoundException("minio");
//        }
//        // 检查存储桶是否已经存在
//        String bucket = Config.getConfigProperty("minio.bucket", "neatlogic");
//        //boolean bucketExists = minioClient.bucketExists(Config.getConfigProperty("minio.bucket", "neatlogic"));
//        boolean bucketExists =
//                minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
//        if (!bucketExists) {
//            // 创建一个名为bucketName的存储桶，用于存储照片等zip文件。
//            //minioClient.makeBucket(Config.getConfigProperty("minio.bucket", "neatlogic"));
//            minioClient.makeBucket(
//                    MakeBucketArgs.builder().bucket(bucket).build()
//            );
//        }
//
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
//        String finalPath = "/" + tenantUuid + "/upload/" + fileParam.getType() + "/" + format.format(new Date()) + "/" + fileParam.getPathName();
//        // 使用putObject上传一个文件到存储桶中
//        //minioClient.putObject(Config.getConfigProperty("minio.bucket", "neatlogic"), finalPath, inputStream, fileParam.getContentType());
//        minioClient.putObject(
//                PutObjectArgs.builder()
//                        .bucket(bucket)
//                        .object(finalPath)
//                        .stream(inputStream, inputStream.available(), -1)
//                        .contentType(fileParam.getContentType())
//                        .build());
////		fileVo.setPath("minio:" + finalPath);
//        return MinioFileSystemHandler.NAME.toLowerCase() + ":" + finalPath;

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        String finalPath = tenantUuid + "/upload/" + fileParam.getType() + "/" + format.format(new Date()) + "/" + fileParam.getPathName();
        return saveData(inputStream, fileParam.getContentType(), finalPath);
    }

    /**
     * 上传文件到固定路径
     *
     * @param inputStream 流
     * @param contentType 文件类型
     * @param filePath    目标路径
     * @return
     * @throws Exception
     */
    @Override
    public String saveData(InputStream inputStream, String contentType, String filePath) throws Exception {
        if (minioClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("minio");
        }
        // 检查存储桶是否已经存在
        String bucket = Config.getConfigProperty("minio.bucket", "neatlogic");
        //boolean bucketExists = minioClient.bucketExists(Config.getConfigProperty("minio.bucket", "neatlogic"));
        boolean bucketExists =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!bucketExists) {
            // 创建一个名为bucketName的存储桶，用于存储照片等zip文件。
            //minioClient.makeBucket(Config.getConfigProperty("minio.bucket", "neatlogic"));
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build()
            );
        }
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        // 使用putObject上传一个文件到存储桶中
        //minioClient.putObject(Config.getConfigProperty("minio.bucket", "neatlogic"), finalPath, inputStream, fileParam.getContentType());
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(filePath)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType(contentType)
                        .build());
        return MinioFileSystemHandler.NAME.toLowerCase() + ":" + filePath;
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
        if (StringUtils.isNotBlank(filePath)) {
            filePath = filePath.replaceAll(NAME.toLowerCase() + ":", "");
            filePath = filePath.replaceAll(NAME.toUpperCase() + ":", "");
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }
            //minioClient.removeObject(Config.getConfigProperty("minio.bucket", "neatlogic"), path);
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(Config.getConfigProperty("minio.bucket", "neatlogic"))
                    .object(filePath).build());

        } else {
            throw new FilePathIllegalException(filePath);
        }

    }

    /**
     * 获取附件输入流
     *
     * @param filePath 附件路径
     * @return 附件输入流
     */
    @Override
    public InputStream getData(String filePath) throws Exception {
        if (minioClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("minio");
        }
        filePath = filePath.replaceAll(NAME.toLowerCase() + ":", "");
        filePath = filePath.replaceAll(NAME.toUpperCase() + ":", "");
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        //return minioClient.getObject(Config.getConfigProperty("minio.bucket", "neatlogic"), path.replaceAll(NAME.toLowerCase() + ":", ""));
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(Config.getConfigProperty("minio.bucket", "neatlogic"))
                .object(filePath).build()
        );
    }

    @Override
    public long getDataLength(String filePath) throws Exception {
        if (minioClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("minio");
        }
        filePath = filePath.replaceAll(NAME.toLowerCase() + ":", "");
        filePath = filePath.replaceAll(NAME.toUpperCase() + ":", "");
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        //return minioClient.statObject(Config.getConfigProperty("minio.bucket", "neatlogic"), filePath.replaceAll(NAME.toLowerCase() + ":", "")).length();
        return minioClient.statObject(StatObjectArgs.builder().bucket(Config.getConfigProperty("minio.bucket", "neatlogic"))
                .object(filePath).build()).size();
    }

    @Override
    public boolean isExit(String filePath) throws Exception {
        if (minioClient == null) {
            throw new FileStorageMediumHandlerNotFoundException("minio");
        }
        filePath = filePath.replaceAll(NAME.toLowerCase() + ":", "");
        filePath = filePath.replaceAll(NAME.toUpperCase() + ":", "");
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        //minioClient.statObject(Config.getConfigProperty("minio.bucket", "neatlogic"), filePath.replaceAll(NAME.toLowerCase() + ":", ""));
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(Config.getConfigProperty("minio.bucket", "neatlogic"))
                    .object(filePath).build());
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            } else {
                throw e;
            }
        }
    }
}

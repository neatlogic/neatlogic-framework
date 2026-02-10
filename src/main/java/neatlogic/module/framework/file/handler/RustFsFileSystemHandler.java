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

import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.file.FilePathIllegalException;
import neatlogic.framework.exception.file.FileStorageMediumHandlerNotFoundException;
import neatlogic.framework.file.core.IFileStorageHandler;
import neatlogic.framework.file.dto.FileVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;

import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

@Component
public class RustFsFileSystemHandler implements InitializingBean, IFileStorageHandler {

    public static final String NAME = "RUSTFS";

    private S3Client s3Client;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(Config.getConfigProperty("rustfs.url"))) {
            String endpoint = Config.getConfigProperty("rustfs.url");
            String accessKey = Config.getConfigProperty("rustfs.accesskey", "rustfsadmin");
            String secretKey = Config.getConfigProperty("rustfs.secretkey", "rustfsadmin");
            String region = Config.getConfigProperty("rustfs.region", "cn-north-1");
            this.s3Client = S3Client.builder()
                    .endpointOverride(URI.create(endpoint))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                    .region(Region.of(region))
                    .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                    .overrideConfiguration(ClientOverrideConfiguration.builder()
                            .apiCallTimeout(Duration.ofSeconds(10))
                            .build())
                    .build();
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
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        String finalPath = tenantUuid + "/upload/" + fileParam.getType() + "/" + format.format(new Date()) + "/" + fileParam.getPathName();
        return saveData(inputStream, fileParam.getContentType(), finalPath, fileParam.getSize());
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
        return saveData(inputStream, contentType, filePath, null);
    }

    private String saveData(InputStream inputStream, String contentType, String filePath, Long size) throws Exception {
        if (s3Client == null) {
            throw new FileStorageMediumHandlerNotFoundException("rustfs");
        }
        String bucket = getBucket();
        ensureBucket(bucket);
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        long contentLength = (size != null && size > 0) ? size : inputStream.available();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
        return RustFsFileSystemHandler.NAME.toLowerCase() + ":" + filePath;
    }

    /**
     * 删除附件
     *
     * @param filePath 附件路径
     */
    public void deleteData(String filePath) throws Exception {
        if (s3Client == null) {
            throw new FileStorageMediumHandlerNotFoundException("rustfs");
        }
        if (StringUtils.isNotBlank(filePath)) {
            filePath = normalizePath(filePath);
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(getBucket())
                    .key(filePath)
                    .build());
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
        if (s3Client == null) {
            throw new FileStorageMediumHandlerNotFoundException("rustfs");
        }
        filePath = normalizePath(filePath);
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(getBucket())
                .key(filePath)
                .build());
    }

    @Override
    public long getDataLength(String filePath) throws Exception {
        if (s3Client == null) {
            throw new FileStorageMediumHandlerNotFoundException("rustfs");
        }
        filePath = normalizePath(filePath);
        HeadObjectResponse response = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(getBucket())
                .key(filePath)
                .build());
        return response.contentLength();
    }

    @Override
    public boolean isExit(String filePath) throws Exception {
        if (s3Client == null) {
            throw new FileStorageMediumHandlerNotFoundException("rustfs");
        }
        filePath = normalizePath(filePath);
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(getBucket())
                    .key(filePath)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    private void ensureBucket(String bucket) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            } else {
                throw e;
            }
        }
    }

    private String getBucket() {
        return Config.getConfigProperty("rustfs.bucket", "neatlogic");
    }

    private String normalizePath(String filePath) {
        filePath = filePath.replaceAll(NAME.toLowerCase() + ":", "");
        filePath = filePath.replaceAll(NAME.toUpperCase() + ":", "");
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        return filePath;
    }
}

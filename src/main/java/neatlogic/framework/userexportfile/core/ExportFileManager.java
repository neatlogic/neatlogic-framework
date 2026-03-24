/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.userexportfile.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.MimeType;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.dao.mapper.UserExportFileMapper;
import neatlogic.framework.userexportfile.dto.UserExportFileVo;
import neatlogic.framework.userexportfile.exception.UserExportingException;
import neatlogic.framework.util.HanyuPinyinUtil;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ExportFileManager {

    private static final Logger logger = LoggerFactory.getLogger(ExportFileManager.class);
    private static final ConcurrentHashMap<String, Object> UNIQUE_KEY_MAP = new ConcurrentHashMap<>();
    private static final int threshold = 10 * 1024 * 1024; // 10MB
    private static final int bufferSize = 1024; // 1KB

    private static UserExportFileMapper userExportFileMapper;

    @Resource
    public void setUserExportFileMapper(UserExportFileMapper _userExportFileMapper) {
        userExportFileMapper = _userExportFileMapper;
    }

    private ExportWriter exportWriter;
    private IUserExportFileType userExportFileType;
    private String name;
    private MimeType mimeType;
    private String uniqueKey;
    private Long exportFileId;
    private long timeout = 0;
    private TimeUnit unit;
    private AtomicBoolean hasTimedOut;

    @FunctionalInterface
    public interface ExportWriter {
        void writeTo(OutputStream out) throws Exception;
    }

    private ExportFileManager() {

    }

    public ExportFileManager(IUserExportFileType userExportFileType) {
        this.userExportFileType = userExportFileType;
        this.exportFileId = SnowflakeUtil.uniqueLong();
        this.hasTimedOut = new AtomicBoolean(false);
    }


    public void generateData(ExportWriter exportWriter) {
        this.exportWriter = exportWriter;
    }

    public boolean exportTo(OutputStream outputStream) throws InterruptedException {
        if (StringUtils.isNotBlank(uniqueKey)) {
            if (UNIQUE_KEY_MAP.containsKey(uniqueKey)) {
                throw new UserExportingException();
            } else {
                UNIQUE_KEY_MAP.put(uniqueKey, StringUtils.EMPTY);
            }
        }
        UserExportFileVo userExportFileVo = new UserExportFileVo(userExportFileType, exportFileId, getName(), getMimeType().getValue());
        userExportFileMapper.insertUserExportFile(userExportFileVo);
        NeatLogicThread neatLogicThread = new NeatLogicThread("EXPORT-MANAGER-" + userExportFileType.getValue()) {
            @Override
            protected void execute() {
                File tempFile = null;
                try (DeferredFileOutputStream dfos = DeferredFileOutputStream.builder()
                        .setBufferSize(bufferSize)
                        .setOutputFile(File.createTempFile(userExportFileType.getValue(),  "-" + exportFileId + "-" + HanyuPinyinUtil.format(getName())))
                        .setThreshold(threshold)
                        .get()) {
                    tempFile = dfos.getFile();
                    exportWriter.writeTo(dfos);
                    dfos.flush();
                    String tenantUuid = TenantContext.get().getTenantUuid();
                    String userId = UserContext.get().getUserId();
                    String yyyyMM = LocalDate.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    String filePath = Config.DATA_HOME() + tenantUuid + File.separator + userId + File.separator + yyyyMM + File.separator + tempFile.getName();
                    if (dfos.isInMemory()) {
                        byte[] bytes = dfos.getData();
                        userExportFileVo.setSize((long) bytes.length);
                        try (InputStream in = new ByteArrayInputStream(bytes)) {
                            String path = FileUtil.saveData(in, userExportFileVo.getContentType(), filePath);
                            userExportFileVo.setPath(path);
                            userExportFileVo.setStatus(UserExportFileVo.Status.DONE.getValue());
                        }
                    } else {
                        userExportFileVo.setSize(tempFile.length());
                        try (InputStream in = new BufferedInputStream(new FileInputStream(tempFile))) {
                            String path = FileUtil.saveData(in, userExportFileVo.getContentType(), filePath);
                            userExportFileVo.setPath(path);
                            userExportFileVo.setStatus(UserExportFileVo.Status.DONE.getValue());
                        }
                    }
                    if (!hasTimedOut.get()) {
                        try {
                            if (dfos.isInMemory()) {
                                try (InputStream inputStream = new ByteArrayInputStream(dfos.getData())) {
                                    IOUtils.copyLarge(inputStream, outputStream);
                                }
                            } else {
                                try (InputStream inputStream = new BufferedInputStream(new FileInputStream(tempFile))) {
                                    IOUtils.copyLarge(inputStream, outputStream);
                                }
                            }
                        } catch (Exception e) {
                            logger.warn(e.getMessage(), e);
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    userExportFileVo.setError(ExceptionUtils.getStackTrace(e));
                    userExportFileVo.setStatus(UserExportFileVo.Status.FAILED.getValue());
                } finally {
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete();
                    }
                    userExportFileVo.setIsEnd(1);
                    userExportFileMapper.insertUserExportFile(userExportFileVo);
                    if (StringUtils.isNotBlank(uniqueKey)) {
                        UNIQUE_KEY_MAP.remove(uniqueKey);
                    }
                }
            }
        };
        CountDownLatch countDownLatch = new CountDownLatch(1);
        neatLogicThread.setCountDownLatch(countDownLatch);
        CachedThreadPool.execute(neatLogicThread);
        try {
            if (timeout > 0 && unit != null) {
                return countDownLatch.await(timeout, unit);
            } else {
                countDownLatch.await();
                return true;
            }
        } finally {
            hasTimedOut.set(true);
        }
    }

    public String getName() {
        if (name == null) {
            name = "exportFile";
        }
        return name;
    }

    public ExportFileManager withName(String name) {
        this.name = name;
        return this;
    }

    public MimeType getMimeType() {
        if (mimeType == null) {
            mimeType = MimeType.STREAM;
        }
        return mimeType;
    }

    public ExportFileManager withMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public ExportFileManager withUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
        return this;
    }

    public ExportFileManager withAwait(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }
}

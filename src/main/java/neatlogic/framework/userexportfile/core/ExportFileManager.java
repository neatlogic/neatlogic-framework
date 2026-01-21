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
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.config.FrameworkTenantConfig;
import neatlogic.framework.dao.mapper.UserExportFileMapper;
import neatlogic.framework.userexportfile.dto.UserExportFileVo;
import neatlogic.framework.userexportfile.exception.UserExportingException;
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
import java.util.function.Supplier;

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

    private Supplier<ExportWriter> supplier;

    private IUserExportFileType userExportFileType;

    private String prefix;

    private String suffix;

    private String contentType;

    private String uniqueKey;

    private DeferredFileOutputStream deferredFileOutputStream;
    @FunctionalInterface
    public interface ExportWriter {
        void writeTo(OutputStream out) throws Exception;
    }
    public ExportFileManager() {

    }

    public ExportFileManager(IUserExportFileType userExportFileType, String prefix, String suffix, String contentType) {
        this.userExportFileType = userExportFileType;
        this.prefix = prefix;
        this.suffix = suffix;
        this.contentType = contentType;
    }


    public void generateData(Supplier<ExportWriter> supplier) {
        this.supplier = supplier;
    }

    public DeferredFileOutputStream export() throws InterruptedException {
        String value = ConfigManager.getConfig(FrameworkTenantConfig.EXPORT_AWAIT_TIME);
        return export(Long.parseLong(value), TimeUnit.SECONDS);
    }

    public DeferredFileOutputStream export(long timeout, TimeUnit unit) throws InterruptedException {
        if (StringUtils.isNotBlank(uniqueKey)) {
            if (UNIQUE_KEY_MAP.containsKey(uniqueKey)) {
                throw new UserExportingException();
            } else {
                UNIQUE_KEY_MAP.put(uniqueKey, StringUtils.EMPTY);
            }
        }
        UserExportFileVo userExportFileVo = new UserExportFileVo(userExportFileType, prefix, suffix, contentType);
        userExportFileMapper.insertUserExportFile(userExportFileVo);
        NeatLogicThread neatLogicThread = new NeatLogicThread("export-" + userExportFileType.getValue() + "-Thread") {
            @Override
            protected void execute() {
                try {
                    //Object t = supplier.get();
                    ExportWriter writer = supplier.get();
                    DeferredFileOutputStream dfos = getDeferredFileOutputStream();
                    writer.writeTo(dfos);
                    dfos.flush();
                    saveData(dfos, userExportFileVo);
                    deferredFileOutputStream = dfos;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    userExportFileVo.setError(ExceptionUtils.getStackTrace(e));
                    userExportFileVo.setStatus(UserExportFileVo.Status.FAILED.getValue());
                } finally {
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
        boolean flag = countDownLatch.await(timeout, unit);
        return deferredFileOutputStream;
    }

    private void saveData(DeferredFileOutputStream dfos, UserExportFileVo userExportFileVo) throws Exception {
        String filePath = generateFilePath(userExportFileVo.getPrefix(), userExportFileVo.getSuffix());
        if (dfos.isInMemory()) {
            byte[] bytes = dfos.getData();
            userExportFileVo.setSize((long) bytes.length);
            try (InputStream in = new ByteArrayInputStream(bytes)) {
                String path = FileUtil.saveData(in, userExportFileVo.getContentType(), filePath);
                userExportFileVo.setPath(path);
                userExportFileVo.setStatus(UserExportFileVo.Status.DONE.getValue());
            }
        } else {
            File tempFile = dfos.getFile();
            userExportFileVo.setSize(tempFile.length());
            try (InputStream in = new BufferedInputStream(new FileInputStream(tempFile))) {
                String path = FileUtil.saveData(in, userExportFileVo.getContentType(), filePath);
                userExportFileVo.setPath(path);
                userExportFileVo.setStatus(UserExportFileVo.Status.DONE.getValue());
            }
        }
    }

    private String generateFilePath(String prefix, String suffix) {
        String tenantUuid = TenantContext.get().getTenantUuid();
        String userId = UserContext.get().getUserId();
        String yyyyMM = LocalDate.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return Config.DATA_HOME() + tenantUuid + File.separator + userId + File.separator + yyyyMM + File.separator + prefix + suffix;
    }

    public DeferredFileOutputStream getDeferredFileOutputStream() throws IOException {
        if (StringUtils.isNotBlank(this.prefix) && StringUtils.isNotBlank(this.suffix)) {
            File tempFile = File.createTempFile(this.prefix, this.suffix);
            return DeferredFileOutputStream.builder().setBufferSize(bufferSize).setOutputFile(tempFile).setThreshold(threshold).get();
        }
        return null;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getContentType() {
        return contentType;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}

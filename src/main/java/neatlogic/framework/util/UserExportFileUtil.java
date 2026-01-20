/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.util;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.mapper.UserExportFileMapper;
import neatlogic.framework.userexportfile.dto.UserExportFileVo;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserExportFileUtil {

    private final static Logger logger = LoggerFactory.getLogger(UserExportFileUtil.class);

    private final static int threshold = 10 * 1024 * 1024; // 10MB

    private final static int bufferSize = 1024; // 1KB
    private static UserExportFileMapper userExportFileMapper;

    @Resource
    public void setUserExportFileMapper(UserExportFileMapper _userExportFileMapper) {
        userExportFileMapper = _userExportFileMapper;
    }

    private static String generateFilePath(String prefix, String suffix) {
        String tenantUuid = TenantContext.get().getTenantUuid();
        String userId = UserContext.get().getUserId();
        String yyyyMM = LocalDate.ofInstant(new Date().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return Config.DATA_HOME() + tenantUuid + File.separator + userId + File.separator + yyyyMM + File.separator + prefix + suffix;
    }

    public static DeferredFileOutputStream getDeferredFileOutputStream(String prefix, String suffix) throws IOException {
        File tmpFile = File.createTempFile(prefix, suffix);
        return DeferredFileOutputStream.builder().setBufferSize(bufferSize).setOutputFile(tmpFile).setThreshold(threshold).get();
    }

    public static String saveWorkbook(
            Workbook workbook,
            UserExportFileVo userExportFileVo,
            HttpServletResponse response
    ) throws Exception {
        return saveWorkbook(workbook, userExportFileVo, response, null);
    }

    public static String saveWorkbook(
            Workbook workbook,
            UserExportFileVo userExportFileVo,
            HttpServletResponse response,
            Map<String, String> headerMap
    ) throws Exception {
        String path = null;
        File tmpFile = File.createTempFile(userExportFileVo.getPrefix(), userExportFileVo.getSuffix());
        try (DeferredFileOutputStream dfos = DeferredFileOutputStream.builder().setBufferSize(bufferSize).setOutputFile(tmpFile).setThreshold(threshold).get()) {
            workbook.write(dfos);
            dfos.flush();
            String filePath = generateFilePath(userExportFileVo.getPrefix(), userExportFileVo.getSuffix());
            if (dfos.isInMemory()) {
                byte[] bytes = dfos.getData();
                userExportFileVo.setSize((long) bytes.length);
                try (InputStream in = new ByteArrayInputStream(bytes)) {
                    path = neatlogic.framework.common.util.FileUtil.saveData(in, userExportFileVo.getContentType(), filePath);
                    userExportFileVo.setPath(path);
                    userExportFileVo.setStatus(UserExportFileVo.Status.DONE.getValue());
                }
            } else {
                userExportFileVo.setSize(tmpFile.length());
                try (InputStream in = new BufferedInputStream(new FileInputStream(tmpFile))) {
                    path = neatlogic.framework.common.util.FileUtil.saveData(in, userExportFileVo.getContentType(), filePath);
                    userExportFileVo.setPath(path);
                    userExportFileVo.setStatus(UserExportFileVo.Status.DONE.getValue());
                }
            }
            try (OutputStream os = response.getOutputStream()) {
                response.setContentType(userExportFileVo.getContentType());
                if (MapUtils.isNotEmpty(headerMap)) {
                    for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                        response.setHeader(entry.getKey(), entry.getValue());
                    }
                }
                String filename = FileUtil.getEncodedFileName(userExportFileVo.getPrefix() + userExportFileVo.getSuffix());
                response.setHeader("Content-Disposition", " attachment; filename=\"" + filename + "\"");
                if (dfos.isInMemory()) {
                    try (InputStream in = new ByteArrayInputStream(dfos.getData())) {
                        IOUtils.copyLarge(in, os);
                    }
                } else {
                    try (InputStream in = new BufferedInputStream(new FileInputStream(tmpFile))) {
                        IOUtils.copyLarge(in, os);
                    }
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            userExportFileVo.setError(ExceptionUtils.getStackTrace(e));
            userExportFileVo.setStatus(UserExportFileVo.Status.FAILED.getValue());
        } finally {
            if (tmpFile.exists()) {
                boolean delete = tmpFile.delete();
            }
            if (workbook != null) {
                ((SXSSFWorkbook) workbook).dispose(); // 清理内存缓存
                workbook.close();
            }
            userExportFileVo.setIsEnd(1);
            userExportFileMapper.insertUserExportFile(userExportFileVo);
        }
        return path;
    }

    public static String saveDeferredFileOutputStream(
            DeferredFileOutputStream deferredFileOutputStream,
            UserExportFileVo userExportFileVo,
            HttpServletResponse response
    ) {
        return saveDeferredFileOutputStream(deferredFileOutputStream, userExportFileVo, response, null);
    }

    public static String saveDeferredFileOutputStream(
            DeferredFileOutputStream deferredFileOutputStream,
            UserExportFileVo userExportFileVo,
            HttpServletResponse response,
            Map<String, String> headerMap
    ) {
        String path = null;
        File tmpFile = deferredFileOutputStream.getFile();
        try (DeferredFileOutputStream dfos = deferredFileOutputStream) {
            String filePath = generateFilePath(userExportFileVo.getPrefix(), userExportFileVo.getSuffix());
            if (dfos.isInMemory()) {
                byte[] bytes = dfos.getData();
                userExportFileVo.setSize((long) bytes.length);
                try (InputStream in = new ByteArrayInputStream(bytes)) {
                    path = neatlogic.framework.common.util.FileUtil.saveData(in, userExportFileVo.getContentType(), filePath);
                    userExportFileVo.setPath(path);
                    userExportFileVo.setStatus(UserExportFileVo.Status.DONE.getValue());
                }
            } else {
                userExportFileVo.setSize(tmpFile.length());
                try (InputStream in = new BufferedInputStream(new FileInputStream(tmpFile))) {
                    path = neatlogic.framework.common.util.FileUtil.saveData(in, userExportFileVo.getContentType(), filePath);
                    userExportFileVo.setPath(path);
                    userExportFileVo.setStatus(UserExportFileVo.Status.DONE.getValue());
                }
            }
            try (OutputStream os = response.getOutputStream()) {
                response.setContentType(userExportFileVo.getContentType());
                if (MapUtils.isNotEmpty(headerMap)) {
                    for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                        response.setHeader(entry.getKey(), entry.getValue());
                    }
                }
                String filename = FileUtil.getEncodedFileName(userExportFileVo.getPrefix() + userExportFileVo.getSuffix());
                response.setHeader("Content-Disposition", " attachment; filename=\"" + filename + "\"");
                if (dfos.isInMemory()) {
                    try (InputStream in = new ByteArrayInputStream(dfos.getData())) {
                        IOUtils.copyLarge(in, os);
                    }
                } else {
                    try (InputStream in = new BufferedInputStream(new FileInputStream(tmpFile))) {
                        IOUtils.copyLarge(in, os);
                    }
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            userExportFileVo.setError(ExceptionUtils.getStackTrace(e));
            userExportFileVo.setStatus(UserExportFileVo.Status.FAILED.getValue());
        } finally {
            if (tmpFile.exists()) {
                boolean delete = tmpFile.delete();
            }
            userExportFileVo.setIsEnd(1);
            userExportFileMapper.insertUserExportFile(userExportFileVo);
        }
        return path;
    }
}

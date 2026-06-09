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

package neatlogic.framework.common.util;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.file.FilePathIllegalException;
import neatlogic.framework.exception.file.FileStorageMediumHandlerNotFoundException;
import neatlogic.framework.exception.file.UploadFileFailedException;
import neatlogic.framework.file.core.FileStorageMediumFactory;
import neatlogic.framework.file.core.IFileStorageHandler;
import neatlogic.framework.file.dto.FileVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Objects;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    /**
     * 根据storageMediumHandler获取存储介质Handler，从而上传到对应的存储介质中
     *
     * @param tenantUuid  租户uuid
     * @param inputStream 文件流
     * @param file        文件
     * @return 附件路径
     * @throws Exception 异常
     */
    public static String saveData(String tenantUuid, InputStream inputStream, FileVo file) throws Exception {
        IFileStorageHandler handler = null;
        String filePath = null;
        try {
            handler = FileStorageMediumFactory.getHandler(Config.FILE_HANDLER());
            if (handler == null) {
                throw new FileStorageMediumHandlerNotFoundException(Config.FILE_HANDLER());
            }
            filePath = handler.saveData(tenantUuid, inputStream, file);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            if (!Objects.equals(Config.FILE_HANDLER(), "FILE")) {
                handler = FileStorageMediumFactory.getHandler("FILE");
                filePath = handler.saveData(tenantUuid, inputStream, file);
            } else {
                throw new UploadFileFailedException(Config.FILE_HANDLER(), ex.getMessage());
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return filePath;
    }

    /**
     * 根据storageMediumHandler获取存储介质Handler，从而上传到对应的存储介质中
     *
     * @param inputStream 文件流
     * @param contentType 文件类型
     * @param filePath 目标路径
     * @return 附件路径
     * @throws Exception 异常
     */
    public static String saveData(InputStream inputStream, String contentType, String filePath) throws Exception {
        try {
            IFileStorageHandler handler = FileStorageMediumFactory.getHandler(Config.FILE_HANDLER());
            if (handler == null) {
                throw new FileStorageMediumHandlerNotFoundException(Config.FILE_HANDLER());
            }
            return handler.saveData(inputStream, contentType, filePath);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            if (!Objects.equals(Config.FILE_HANDLER(), "FILE")) {
                IFileStorageHandler handler = FileStorageMediumFactory.getHandler("FILE");
                return handler.saveData(inputStream, contentType, filePath);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }

    /**
     * 获取附件
     *
     * @param filePath 附件路径
     * @return 附件流
     * @throws Exception 异常
     */
    public static InputStream getData(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath) || !filePath.contains(":")) {
            throw new FilePathIllegalException(filePath);
        }
        String prefix = filePath.split(":")[0];
        IFileStorageHandler handler = FileStorageMediumFactory.getHandler(prefix.toUpperCase());
        if (handler == null) {
            throw new FileStorageMediumHandlerNotFoundException(prefix);
        }
        return handler.getData(filePath);
    }


    /**
     * 删除附件
     *
     * @param filePath 附件路径
     * @throws Exception 异常
     */
    public static void deleteData(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath) || !filePath.contains(":")) {
            throw new FilePathIllegalException(filePath);
        }
        String prefix = filePath.split(":")[0];
        IFileStorageHandler handler = FileStorageMediumFactory.getHandler(prefix.toUpperCase());
        if (handler == null) {
            throw new FileStorageMediumHandlerNotFoundException(prefix);
        }
        handler.deleteData(filePath);
    }

    /**
     * @param filePath 文件路径
     * @return 附件大小
     * @throws Exception 异常
     */
    public static long getDataLength(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath) || !filePath.contains(":")) {
            throw new FilePathIllegalException(filePath);
        }
        String prefix = filePath.split(":")[0];
        IFileStorageHandler handler = FileStorageMediumFactory.getHandler(prefix.toUpperCase());
        if (handler == null) {
            throw new FileStorageMediumHandlerNotFoundException(prefix);
        }
        return handler.getDataLength(filePath);
    }

    /**
     * 判断附件是否存在
     * @param filePath 附件路径
     * @return
     * @throws Exception
     */
    public static boolean exists(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath) || !filePath.contains(":")) {
            throw new FilePathIllegalException(filePath);
        }
        String prefix = filePath.split(":")[0];
        IFileStorageHandler handler = FileStorageMediumFactory.getHandler(prefix);
        if (handler == null) {
            throw new FileStorageMediumHandlerNotFoundException(prefix);
        }
        return handler.isExit(filePath);
    }
}

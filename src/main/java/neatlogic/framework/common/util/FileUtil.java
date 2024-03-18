/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.common.util;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.file.FilePathIllegalException;
import neatlogic.framework.exception.file.FileStorageMediumHandlerNotFoundException;
import neatlogic.framework.file.core.FileStorageMediumFactory;
import neatlogic.framework.file.core.IFileStorageHandler;
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
     * @param fileId      文件id
     * @param contentType 附件类型
     * @param fileType    附件类型
     * @return 附件路径
     * @throws Exception 异常
     */
    public static String saveData(String tenantUuid, InputStream inputStream, String fileId, String contentType, String fileType) throws Exception {
        IFileStorageHandler handler = null;
        String filePath = null;
        try {
            handler = FileStorageMediumFactory.getHandler(Config.FILE_HANDLER());
            if (handler == null) {
                throw new FileStorageMediumHandlerNotFoundException(Config.FILE_HANDLER());
            }
            filePath = handler.saveData(tenantUuid, inputStream, fileId, contentType, fileType);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            if (!Objects.equals(Config.FILE_HANDLER(), "FILE")) {
                handler = FileStorageMediumFactory.getHandler("FILE");
                filePath = handler.saveData(tenantUuid, inputStream, fileId, contentType, fileType);
            }
        }
        return filePath;
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

}

/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.common.util;

import neatlogic.framework.exception.file.FilePathIllegalException;
import neatlogic.framework.exception.file.FileStorageMediumHandlerNotFoundException;
import neatlogic.framework.file.core.FileStorageMediumFactory;
import neatlogic.framework.file.core.IFileStorageHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;

public class FileUtil {
    //private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    /**
     * 根据storageMediumHandler获取存储介质Handler，从而上传到对应的存储介质中
     *
     * @param storageMediumHandler 介质控制器
     * @param tenantUuid           租户uuid
     * @param inputStream          文件流
     * @param fileId               文件id
     * @param contentType          附件类型
     * @param fileType             附件类型
     * @return 附件路径
     * @throws Exception 异常
     */
    public static String saveData(String storageMediumHandler, String tenantUuid, InputStream inputStream, String fileId, String contentType, String fileType) throws Exception {
        IFileStorageHandler handler = FileStorageMediumFactory.getHandler(storageMediumHandler);
        if (handler == null) {
            throw new FileStorageMediumHandlerNotFoundException(storageMediumHandler);
        }
        return handler.saveData(tenantUuid, inputStream, fileId, contentType, fileType);
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

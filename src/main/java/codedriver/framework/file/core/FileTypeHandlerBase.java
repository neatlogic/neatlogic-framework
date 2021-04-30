/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileTypeHandlerBase implements IFileTypeHandler {
    private final static Logger logger = LoggerFactory.getLogger(FileTypeHandlerBase.class);

    @Override
    public final void deleteFile(Long fileId) throws Exception {
        myDeleteFile(fileId);
        try {
            FileManager.deleteFileById(fileId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    protected abstract void myDeleteFile(Long fileId);
}

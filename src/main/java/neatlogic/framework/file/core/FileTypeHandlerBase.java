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

package neatlogic.framework.file.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.util.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FileTypeHandlerBase implements IFileTypeHandler {
    private static final Logger logger = LoggerFactory.getLogger(FileTypeHandlerBase.class);
    protected IFileManager fileManager;

    @Autowired
    public void setFileManager(FileManager _fileManager) {
        fileManager = _fileManager;
    }

    @Override
    public final void deleteFile(FileVo fileVo, JSONObject paramObj) throws Exception {
        if (myDeleteFile(fileVo, paramObj)) {
            try {
                fileManager.deleteFile(fileVo.getId());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public final String getUniqueKey(String key) {
        if (key != null) {
            if (key.length() != 32) {
                return Md5Util.encryptMD5(key);
            } else {
                return key;
            }
        }
        return null;
    }

    /**
     * 各附件类型处理器执行自己的删除逻辑，通过返回值告诉框架是否允许真正删除附件
     *
     * @param fileVo 附件
     * @return true：允许继续删除附件，false：不允许删除附件
     */
    protected abstract boolean myDeleteFile(FileVo fileVo, JSONObject paramObj);
}

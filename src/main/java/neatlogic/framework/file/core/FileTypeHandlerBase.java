/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.file.core;

import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.util.Md5Util;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileTypeHandlerBase implements IFileTypeHandler {
    private final static Logger logger = LoggerFactory.getLogger(FileTypeHandlerBase.class);

    @Override
    public final void deleteFile(FileVo fileVo, JSONObject paramObj) throws Exception {
        if (myDeleteFile(fileVo, paramObj)) {
            try {
                FileManager.deleteFileById(fileVo.getId());
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

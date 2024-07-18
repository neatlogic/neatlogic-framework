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

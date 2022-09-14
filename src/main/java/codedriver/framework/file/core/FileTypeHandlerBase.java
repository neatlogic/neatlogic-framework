/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

import codedriver.framework.file.dto.FileVo;
import codedriver.framework.util.Md5Util;
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

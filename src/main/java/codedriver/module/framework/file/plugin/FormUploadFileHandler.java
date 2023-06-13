package codedriver.module.framework.file.plugin;

import codedriver.framework.file.core.FileTypeHandlerBase;
import codedriver.framework.file.dto.FileVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class FormUploadFileHandler extends FileTypeHandlerBase {

    @Override
    protected boolean myDeleteFile(FileVo fileVo, JSONObject paramObj) {
        return false;
    }

    @Override
    public boolean valid(String userUuid, FileVo fileVo, JSONObject jsonObj) throws Exception {
        return true;
    }

    @Override
    public String getName() {
        return "FORMUPLOADFILE";
    }

    @Override
    public String getDisplayName() {
        return "表单上传文件";
    }
}

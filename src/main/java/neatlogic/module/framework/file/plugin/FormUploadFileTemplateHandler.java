package neatlogic.module.framework.file.plugin;

import neatlogic.framework.file.core.FileTypeHandlerBase;
import neatlogic.framework.file.dto.FileVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class FormUploadFileTemplateHandler extends FileTypeHandlerBase {

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
        return "FORMUPLOADFILETEMPLATE";
    }

    @Override
    public String getDisplayName() {
        return "表单上传文件模板";
    }
}

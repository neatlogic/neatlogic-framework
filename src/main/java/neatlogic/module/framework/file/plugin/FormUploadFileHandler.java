package neatlogic.module.framework.file.plugin;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.file.core.FileTypeHandlerBase;
import neatlogic.framework.util.$;
import neatlogic.framework.file.dto.FileVo;
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

    /** 返回当前语言环境下的文件类型显示名称。 */
    @Override
    public String getDisplayName() {
        return $.t("file.handler.formuploadfilehandler.displayname");
    }
}

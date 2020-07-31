package codedriver.framework.restful.audit;

import codedriver.framework.file.core.FileTypeHandlerBase;
import codedriver.framework.file.dto.FileVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class ApiAuditFileHandler extends FileTypeHandlerBase {

	@Override
	public boolean valid(String userUuid, JSONObject jsonObj) {
		return true;
	}

	@Override
	public String getDisplayName() {
		return "操作审计记录文件";
	}

	@Override
	public void afterUpload(FileVo fileVo, JSONObject jsonObj) {
	}

	@Override
	public String getName() {
		return "API_AUDIT";
	}

}

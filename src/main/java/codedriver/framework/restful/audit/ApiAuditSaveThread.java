package codedriver.framework.restful.audit;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.util.AuditUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiAuditSaveThread extends CodeDriverThread {

	private static ApiMapper apiMapper;

	@Autowired
	public void setApiMapper(ApiMapper _apiMapper) {
		apiMapper = _apiMapper;
	}

	private ApiAuditVo apiAuditVo;

	public ApiAuditSaveThread(ApiAuditVo _apiAuditVo) {
		apiAuditVo = _apiAuditVo;
	}

	public ApiAuditSaveThread() {

	}

	@Override
	protected void execute() {
		if (apiAuditVo != null) {
			AuditUtil.saveAuditDetail(apiAuditVo,"api_audit");
			apiMapper.insertApiAudit(apiAuditVo);
		}
	}

}

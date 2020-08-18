package codedriver.framework.integration.core;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.integration.dao.mapper.IntegrationMapper;
import codedriver.framework.integration.dto.IntegrationAuditVo;
import codedriver.framework.util.AuditUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntegrationAuditSaveThread extends CodeDriverThread {

	private static IntegrationMapper integrationMapper;

	@Autowired
	public void setIntegrationMapper(IntegrationMapper _integrationMapper) {
		integrationMapper = _integrationMapper;
	}

	private IntegrationAuditVo integrationAuditVo;

	public IntegrationAuditSaveThread(IntegrationAuditVo _integrationAuditVo) {
		integrationAuditVo = _integrationAuditVo;
	}

	public IntegrationAuditSaveThread() {

	}

	@Override
	protected void execute() {
		if (integrationAuditVo != null) {
			AuditUtil.saveAuditDetail(integrationAuditVo,"integration_audit");
			integrationMapper.insertIntegrationAudit(integrationAuditVo);
		}
	}

}

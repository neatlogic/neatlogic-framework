package codedriver.framework.integration.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.integration.dao.mapper.IntegrationMapper;
import codedriver.framework.integration.dto.IntegrationAuditVo;

@Service
public class IntegrationAuditSaveThread extends CodeDriverThread {
	Logger logger = LoggerFactory.getLogger(IntegrationAuditSaveThread.class);

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
			integrationMapper.insertIntegrationAudit(integrationAuditVo);
			if (StringUtils.isNotBlank(integrationAuditVo.getErrorHash())) {
				integrationMapper.replaceIntegrationAuditDetail(integrationAuditVo.getErrorHash(), integrationAuditVo.getError());
			}
			if (StringUtils.isNotBlank(integrationAuditVo.getResultHash())) {
				integrationMapper.replaceIntegrationAuditDetail(integrationAuditVo.getResultHash(), integrationAuditVo.getResult());
			}
			if (StringUtils.isNotBlank(integrationAuditVo.getParamHash())) {
				integrationMapper.replaceIntegrationAuditDetail(integrationAuditVo.getParamHash(), integrationAuditVo.getParam());
			}
		}
	}

}

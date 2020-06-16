package codedriver.framework.restful.audit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiAuditVo;

@Service
public class ApiAuditSaveThread extends CodeDriverThread {
	Logger logger = LoggerFactory.getLogger(ApiAuditSaveThread.class);

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
			apiMapper.insertApiAudit(apiAuditVo);
			if (StringUtils.isNotBlank(apiAuditVo.getErrorHash())) {
				apiMapper.replaceApiAuditDetail(apiAuditVo.getErrorHash(), apiAuditVo.getError());
			}
			if (StringUtils.isNotBlank(apiAuditVo.getResultHash())) {
				apiMapper.replaceApiAuditDetail(apiAuditVo.getResultHash(), JSON.toJSONString(apiAuditVo.getResult()));
			}
			if (StringUtils.isNotBlank(apiAuditVo.getParamHash())) {
				apiMapper.replaceApiAuditDetail(apiAuditVo.getParamHash(), apiAuditVo.getParam());
			}
		}
	}

}

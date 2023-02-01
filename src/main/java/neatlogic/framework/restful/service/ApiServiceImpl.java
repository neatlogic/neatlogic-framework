package neatlogic.framework.restful.service;

import neatlogic.framework.restful.dao.mapper.ApiAuditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiVo;

@Service
public class ApiServiceImpl implements ApiService {

	@Autowired
	private ApiMapper apiMapper;

	@Autowired
	private ApiAuditMapper apiAuditMapper;

	@Override
	public ApiVo getApiByToken(String token) {
		return apiMapper.getApiByToken(token);
	}


	@Override
	public int saveApiAccessCount(String token, int count) {
		if(apiAuditMapper.getApiAccessCountLockByToken(token) == null) {
			return apiAuditMapper.insertApiAccessCount(token, count);
		}else {
			return apiAuditMapper.updateApiAccessCount(token, count);
		}
	}
}

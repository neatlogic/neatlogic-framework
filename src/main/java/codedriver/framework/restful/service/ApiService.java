package codedriver.framework.restful.service;

import org.springframework.transaction.annotation.Transactional;

import codedriver.framework.restful.dto.ApiVo;

public interface ApiService {
	public ApiVo getApiByToken(String token);
	
	@Transactional
	public int udpateApiAccessCount(String token, int count);
}

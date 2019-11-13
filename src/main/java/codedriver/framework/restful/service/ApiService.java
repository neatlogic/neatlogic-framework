package codedriver.framework.restful.service;

import codedriver.framework.restful.dto.ApiVo;

public interface ApiService {
	public ApiVo getApiByToken(String token);
}

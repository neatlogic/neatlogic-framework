package codedriver.framework.restful.service;

import codedriver.framework.restful.dto.ApiVo;

public interface ApiService {
	public ApiVo getApiByToken(String token);

	/**
	 * 记录API访问次数方法
	 * @param token
	 */
	public void saveApiAccessCount(String token);
}

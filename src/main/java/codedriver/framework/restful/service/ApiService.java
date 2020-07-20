package codedriver.framework.restful.service;

import org.springframework.transaction.annotation.Transactional;

import codedriver.framework.restful.dto.ApiVo;

public interface ApiService {
	public ApiVo getApiByToken(String token);
	/**
	 * 
	* @Time:2020年7月18日
	* @Description: 保存接口访问次数
	* @param token
	* @param count
	* @return int
	 */
	@Transactional
	public int saveApiAccessCount(String token, int count);
}

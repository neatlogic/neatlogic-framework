package codedriver.framework.restful.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;

@Service
public class ApiServiceImpl implements ApiService {

	@Autowired
	private ApiMapper apiMapper;


	@Override
	public ApiVo getApiByToken(String token) {
		return apiMapper.getApiByToken(token);
	}
}

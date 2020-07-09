package codedriver.framework.restful.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApiServiceImpl implements ApiService {

	@Autowired
	private ApiMapper apiMapper;


	@Override
	public ApiVo getApiByToken(String token) {
		return apiMapper.getApiByToken(token);
	}

	@Override
	@Transactional
	public void saveApiAccessCount(String token) {
		ApiVo vo = apiMapper.getApiAccessCountLockByToken(token);
		if(vo != null){
			vo.setVisitTimes(vo.getVisitTimes() + 1);
		}else{
			vo = new ApiVo();
			vo.setToken(token);
			vo.setVisitTimes(1);
		}
		apiMapper.replaceApiAccessCount(vo);
	}
}

package codedriver.framework.restful.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.restful.core.ApiComponentBase;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;

@Service
public class TestApi extends ApiComponentBase {

	@Autowired
	private ApiMapper apiMapper;

	@Override
	public String getToken() {
		return "/test";
	}

	@Override
	public String getName() {
		return "测试API";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		ApiVo apiVo = apiMapper.getApiByToken("test");
		return apiVo;
	}

}

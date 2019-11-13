package codedriver.framework.restful.api;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.restful.core.ApiComponentBase;

@Service
public class TestApi extends ApiComponentBase {
	

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
		return "OK";
	}

}

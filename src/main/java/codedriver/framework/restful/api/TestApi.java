package codedriver.framework.restful.api;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.AuthAction;
import codedriver.framework.restful.core.ApiComponentBase;

@Service
@AuthAction(name = "SYSTEM_MENU_EDIT")
public class TestApi extends ApiComponentBase {
	

	@Override
	public String getToken() {
		return "test";
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
		return "OK1";
	}

}

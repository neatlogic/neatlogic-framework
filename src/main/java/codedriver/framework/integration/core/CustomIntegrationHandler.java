package codedriver.framework.integration.core;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;

@Component
public class CustomIntegrationHandler extends IntegrationHandlerBase<JSONArray> {
	public String getName() {
		return "自定义";
	}

	public Object myGetData() {
		return null;
	}

	@Override
	public JSONArray getInputPattern() {
		return null;
	}

	@Override
	public JSONArray getOutputPattern() {
		return null;
	}

	@Override
	protected JSONArray myGetData(String result) {
		return null;
	}

	@Override
	public Boolean allowCustomPattern() {
		return true;
	}

}

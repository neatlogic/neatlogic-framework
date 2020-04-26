package codedriver.framework.integration.core;

import java.util.List;

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
	public List getInputPatternList() {
		return null;
	}

	@Override
	public List getOutputPatternList() {
		return null;
	}

	@Override
	protected JSONArray myGetData(JSONArray result) {
		return result;
	}

	@Override
	public Boolean allowCustomPattern() {
		return true;
	}

}

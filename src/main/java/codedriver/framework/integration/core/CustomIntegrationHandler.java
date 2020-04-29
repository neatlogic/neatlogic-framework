package codedriver.framework.integration.core;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;

import codedriver.framework.integration.dto.IntegrationVo;

@Component
public class CustomIntegrationHandler extends IntegrationHandlerBase {
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
	protected void beforeSend(IntegrationVo integrationVo) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void afterReturn(IntegrationVo integrationVo) {
		// TODO Auto-generated method stub

	}

}

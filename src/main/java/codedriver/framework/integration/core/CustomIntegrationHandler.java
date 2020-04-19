package codedriver.framework.integration.core;

import java.util.List;

import org.springframework.stereotype.Component;

import codedriver.framework.integration.dto.IntegrationVo;

@Component
public class CustomIntegrationHandler<Object> extends IntegrationHandlerBase {
	public String getName() {
		return "自定义";
	}

	public Object myGetData() {
		return null;
	}

	@Override
	public List getInputPartternList() {
		return null;
	}

	@Override
	public List getOutputPartternList() {
		return null;
	}

	@Override
	protected Object myGetData(IntegrationVo integrationVo) {
		// TODO Auto-generated method stub
		return null;
	}

}

package codedriver.framework.integration.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import codedriver.framework.integration.dao.mapper.IntegrationMapper;
import codedriver.framework.integration.dto.IntegrationInvokeDetailVo;
import codedriver.framework.integration.dto.IntegrationVo;

@Component
public class TestInvoker extends IntegrationInvokerBase<String> {

	@Autowired
	private IntegrationMapper integrationMapper;

	public TestInvoker(Object _key) {
		super(_key);
	}

	private TestInvoker() {

	}

	@Override
	public String getType() {
		return "测试类型";
	}

	@Override
	public IntegrationInvokeDetailVo getInvokeDetail(String key) {
		IntegrationVo integrationVo = integrationMapper.getIntegrationByUuid(key);
		IntegrationInvokeDetailVo detailVo = new IntegrationInvokeDetailVo();
		detailVo.setName(integrationVo.getName());
		return detailVo;
	}

}

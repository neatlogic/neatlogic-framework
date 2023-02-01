/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.integration.handler;

import neatlogic.framework.integration.core.IntegrationInvokerBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationInvokeDetailVo;
import neatlogic.framework.integration.dto.IntegrationVo;

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

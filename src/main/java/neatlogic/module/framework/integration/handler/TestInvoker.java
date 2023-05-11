/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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

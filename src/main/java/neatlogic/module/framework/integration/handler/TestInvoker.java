/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.integration.handler;

import neatlogic.framework.integration.core.IntegrationInvokerBase;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationInvokeDetailVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

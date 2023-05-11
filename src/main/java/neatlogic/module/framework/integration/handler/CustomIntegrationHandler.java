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

import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.integration.core.IntegrationHandlerBase;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.PatternVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomIntegrationHandler extends IntegrationHandlerBase {
	public String getName() {
		return "自定义";
	}

	public Object myGetData() {
		return null;
	}

	@Override
	public List<PatternVo> getInputPattern() {
		return null;
	}

	@Override
	public List<PatternVo> getOutputPattern() {
		return null;
	}

	@Override
	protected void beforeSend(IntegrationVo integrationVo) {

	}

	@Override
	protected void afterReturn(IntegrationVo integrationVo) {

	}

	@Override
	public Integer hasPattern() {
		return 0;
	}

	@Override
	public void validate(IntegrationResultVo resultVo) throws ApiRuntimeException {

	}
}

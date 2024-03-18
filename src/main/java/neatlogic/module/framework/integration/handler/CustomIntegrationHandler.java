/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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

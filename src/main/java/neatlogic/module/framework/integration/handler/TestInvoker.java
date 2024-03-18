/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

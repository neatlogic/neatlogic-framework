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

package neatlogic.module.framework.restful.api;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.core.AuthFactory;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.AuthGroupVo;
import neatlogic.framework.dto.AuthVo;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

@OperationType(type = OperationTypeEnum.SEARCH)
public class AuthSearchApi extends PrivateApiComponentBase {

	@Override
	public String getToken() {
		return "auth/search";
	}

	@Override
	public String getName() {
		return "权限列表查询接口";
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Input({ @Param(name = "groupName", type = ApiParamType.STRING, desc = "权限组名") })
	@Output({ @Param(name = "authGroupList", type = ApiParamType.JSONARRAY, desc = "权限列表组集合", explode = AuthGroupVo[].class) })
	@Description(desc = "权限列表查询接口")
	@Override
	public Object myDoService(JSONObject jsonObj) throws Exception {
		JSONObject returnObj = new JSONObject();
		String groupName = jsonObj.getString("groupName");
		List<AuthGroupVo> authGroupVoList = new ArrayList<>();
		Map<String, List<AuthBase>> authGroupMap = AuthFactory.getAuthGroupMap();
		for (Map.Entry<String, List<AuthBase>> entry : authGroupMap.entrySet()) {
			String authGroupName = entry.getKey();
			if (!TenantContext.get().getActiveModuleMap().containsKey(authGroupName) || (groupName != null && !groupName.equalsIgnoreCase(authGroupName))) {
				continue;
			}
			AuthGroupVo authGroupVo = new AuthGroupVo();
			authGroupVo.setName(authGroupName);
			authGroupVo.setDisplayName(ModuleUtil.getModuleGroup(authGroupName).getGroupName());
			List<AuthBase> authList = authGroupMap.get(authGroupName);
			if (authList != null && authList.size() > 0) {
				List<AuthVo> authArray = new ArrayList<>();
				for (AuthBase authBase : authList) {
					AuthVo authVo = new AuthVo();
					authVo.setName(authBase.getAuthName());
					authVo.setDisplayName(authBase.getAuthDisplayName());
					authArray.add(authVo);
				}
				authGroupVo.setAuthVoList(authArray);
			}
			authGroupVoList.add(authGroupVo);
		}
		returnObj.put("authGroupList", authGroupVoList);
		return returnObj;
	}
}

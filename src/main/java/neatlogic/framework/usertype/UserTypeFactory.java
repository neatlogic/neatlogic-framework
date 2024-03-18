/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.usertype;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.IUserType;
import neatlogic.framework.dto.UserTypeVo;
import neatlogic.framework.dto.module.ModuleGroupVo;
import org.reflections.Reflections;

import java.util.*;

public class UserTypeFactory {
	private static final Map<String, UserTypeVo> userTypeMap = new HashMap<>();

	static {
		Reflections reflections = new Reflections("neatlogic");
		Set<Class<? extends IUserType>> userTypeClass = reflections.getSubTypesOf(IUserType.class);
		for (Class<? extends IUserType> c : userTypeClass) {
			try {
				Object[] objects = c.getEnumConstants();
				UserTypeVo userTypeVo = (UserTypeVo) c.getMethod("getUserType").invoke(objects[0]);
				userTypeMap.put(userTypeVo.getModuleId(), userTypeVo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static Map<String,UserTypeVo> getUserTypeMap() {
		Map<String, UserTypeVo> resultUserTypeMap = new HashMap<>();
		List<ModuleGroupVo> groupVoList = TenantContext.get().getActiveModuleGroupList();
		for (Map.Entry<String, UserTypeVo> entry : userTypeMap.entrySet()) {
			String moduleGroup = entry.getKey();
			if (groupVoList.stream().anyMatch(o -> Objects.equals(moduleGroup, o.getGroup()))) {
				resultUserTypeMap.put(moduleGroup,entry.getValue());
			}
		}
		return resultUserTypeMap;
	}
	
}

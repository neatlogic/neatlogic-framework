/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.usertype;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.IUserType;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.UserTypeVo;
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

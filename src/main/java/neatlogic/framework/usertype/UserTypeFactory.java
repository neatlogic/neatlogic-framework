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

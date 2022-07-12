/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.usertype;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.constvalue.IUserType;
import codedriver.framework.dto.module.ModuleGroupVo;
import codedriver.framework.dto.UserTypeVo;
import org.reflections.Reflections;

import java.util.*;

public class UserTypeFactory {
	private static final Map<String, UserTypeVo> userTypeMap = new HashMap<>();

	static {
		Reflections reflections = new Reflections("codedriver");
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

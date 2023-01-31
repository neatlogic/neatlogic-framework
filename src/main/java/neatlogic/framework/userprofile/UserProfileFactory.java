/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.userprofile;

import neatlogic.framework.common.constvalue.IUserProfile;
import neatlogic.framework.dto.UserProfileVo;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserProfileFactory {
	private static final Map<String, UserProfileVo> userProfileMap = new HashMap<>();

	static {
		Reflections reflections = new Reflections("neatlogic");
		Set<Class<? extends IUserProfile>> userProfileClass = reflections.getSubTypesOf(IUserProfile.class);
		for (Class<? extends IUserProfile> c : userProfileClass) {
			try {
				Object[] objects = c.getEnumConstants();
				UserProfileVo userProfileVo = (UserProfileVo) c.getMethod("getUserProfile").invoke(objects[0]);
				userProfileMap.put(userProfileVo.getModuleId(), userProfileVo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static Map<String,UserProfileVo> getUserProfileMap() {
		return userProfileMap;
	}
	
}

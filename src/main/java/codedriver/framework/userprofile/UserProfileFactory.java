package codedriver.framework.userprofile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import codedriver.framework.dto.UserProfileVo;
import codedriver.framework.restful.api.IUserProfile;

public class UserProfileFactory {
	private static Map<String, UserProfileVo> userProfileMap = new HashMap<String, UserProfileVo>();
	static {
		Reflections reflections = new Reflections("codedriver");
		Set<Class<? extends IUserProfile>> userProfileClass = reflections.getSubTypesOf(IUserProfile.class);
		for (Class<? extends IUserProfile> c: userProfileClass) {
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

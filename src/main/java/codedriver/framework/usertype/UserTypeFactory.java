package codedriver.framework.usertype;

import codedriver.framework.common.constvalue.IUserType;
import codedriver.framework.dto.UserTypeVo;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserTypeFactory {
	private static Map<String, UserTypeVo> userTypeMap = new HashMap<>();
	static {
		Reflections reflections = new Reflections("codedriver");
		Set<Class<? extends IUserType>> userTypeClass = reflections.getSubTypesOf(IUserType.class);
		for (Class<? extends IUserType> c: userTypeClass) {
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
		return userTypeMap;
	}
	
}

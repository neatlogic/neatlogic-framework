package codedriver.framework.param.validate.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.constvalue.ParamType;

public class ParamValidatorFactory {
	private static Map<ApiParamType, ApiParamValidatorBase> authParamMap = new HashMap<>();
	private static Map<ParamType, ParamValidatorBase> authParamMap2 = new HashMap<>();

	static {
		Reflections reflections = new Reflections("codedriver");
		Set<Class<? extends ApiParamValidatorBase>> authClass = reflections.getSubTypesOf(ApiParamValidatorBase.class);
		for (Class<? extends ApiParamValidatorBase> c: authClass) {
			try {
				ApiParamValidatorBase authIns = c.newInstance();
				authParamMap.put(authIns.getType(), authIns);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Set<Class<? extends ParamValidatorBase>> authClass2 = reflections.getSubTypesOf(ParamValidatorBase.class);
		for (Class<? extends ParamValidatorBase> c: authClass2) {
			try {
				ParamValidatorBase authIns = c.newInstance();
				authParamMap2.put(authIns.getType(), authIns);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static ApiParamValidatorBase getAuthInstance(ApiParamType authParamType) {
		return authParamMap.get(authParamType);
	}
	
	public static ParamValidatorBase getAuthInstance(ParamType basicType) {
		return authParamMap2.get(basicType);
	}
}

/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.param.validate.core;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.ParamType;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParamValidatorFactory {
	private static final Map<ApiParamType, ApiParamValidatorBase> authParamMap = new HashMap<>();
	private static final Map<ParamType, ParamValidatorBase> authParamMap2 = new HashMap<>();

	static {
		Reflections reflections = new Reflections("neatlogic");
		Set<Class<? extends ApiParamValidatorBase>> authClass = reflections.getSubTypesOf(ApiParamValidatorBase.class);
		for (Class<? extends ApiParamValidatorBase> c : authClass) {
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

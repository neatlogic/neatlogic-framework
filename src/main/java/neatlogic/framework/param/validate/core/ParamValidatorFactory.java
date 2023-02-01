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

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

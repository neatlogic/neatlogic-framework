/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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

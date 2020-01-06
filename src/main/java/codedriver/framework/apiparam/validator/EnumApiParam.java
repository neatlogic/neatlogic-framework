package codedriver.framework.apiparam.validator;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.apiparam.core.ApiParamBase;
import codedriver.framework.apiparam.core.ApiParamType;

public class EnumApiParam extends ApiParamBase {

	@Override
	public String getName() {
		return "枚举";
	}

	@Override
	public boolean validate(Object param, String rule) {
		if (StringUtils.isNotBlank(rule)) {
			if (rule.contains(",")) {
				for (String r : rule.split(",")) {
					if (param.toString().equals(r)) {
						return true;
					}
				}
			} else {
				if (param.equals(rule)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ApiParamType getType() {
		return ApiParamType.ENUM;
	}

}

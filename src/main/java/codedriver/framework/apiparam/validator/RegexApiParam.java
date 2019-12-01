package codedriver.framework.apiparam.validator;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.apiparam.core.ApiParamBase;
import codedriver.framework.apiparam.core.ApiParamType;

public class RegexApiParam extends ApiParamBase {

	@Override
	public String getName() {
		return "正则表达式";
	}

	@Override
	public boolean validate(Object param, String rule) {
		if (StringUtils.isNotBlank(rule)) {
			Pattern pattern = Pattern.compile(rule);
			return pattern.matcher(param.toString()).matches();
		} else {
			return true;
		}
	}

	@Override
	public ApiParamType getType() {
		return ApiParamType.REGEX;
	}

}

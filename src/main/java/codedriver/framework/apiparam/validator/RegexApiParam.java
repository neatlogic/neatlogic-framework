package codedriver.framework.apiparam.validator;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.param.validate.core.ApiParamValidatorBase;

public class RegexApiParam extends ApiParamValidatorBase {

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

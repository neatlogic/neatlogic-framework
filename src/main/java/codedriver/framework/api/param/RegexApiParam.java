package codedriver.framework.api.param;

import java.util.regex.Pattern;

import codedriver.framework.api.core.ApiParamBase;
import codedriver.framework.api.core.ApiParamType;

public class RegexApiParam extends ApiParamBase {

	@Override
	public String getName() {
		return "手机号码";
	}

	@Override
	public boolean validate(Object param) {
		Pattern pattern = Pattern.compile("^1(3|4|5|7|8)\\d{9}$");
		return pattern.matcher(param.toString()).matches();
	}

	@Override
	public ApiParamType getType() {
		return ApiParamType.TELEPHONE;
	}

}

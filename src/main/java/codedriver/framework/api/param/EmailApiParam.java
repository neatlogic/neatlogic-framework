package codedriver.framework.api.param;

import java.util.regex.Pattern;

import codedriver.framework.api.core.ApiParamBase;
import codedriver.framework.api.core.ApiParamType;

public class EmailApiParam extends ApiParamBase {

	@Override
	public String getName() {

		return "邮箱地址";
	}

	@Override
	public boolean validate(Object param) {
		Pattern pattern = Pattern.compile("^[A-Za-z0-9]+([_\\.][A-Za-z0-9]+)*@([A-Za-z0-9\\-]+\\.)+[A-Za-z]{2,6}$");
		return pattern.matcher(param.toString()).matches();
	}

	@Override
	public ApiParamType getType() {
		return ApiParamType.EMAIL;
	}

}

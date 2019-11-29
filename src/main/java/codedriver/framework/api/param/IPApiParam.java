package codedriver.framework.api.param;

import java.util.regex.Pattern;

import codedriver.framework.api.core.ApiParamBase;
import codedriver.framework.api.core.ApiParamType;

public class IPApiParam extends ApiParamBase {

	@Override
	public String getName() {

		return "ip";
	}

	@Override
	public boolean validate(Object param) {
		Pattern pattern = Pattern.compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]" + "|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
		return pattern.matcher(param.toString()).matches();
	}

	@Override
	public ApiParamType getType() {
		return ApiParamType.IP;
	}

}

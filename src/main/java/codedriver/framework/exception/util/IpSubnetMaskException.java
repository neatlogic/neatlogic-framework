package codedriver.framework.exception.util;

import codedriver.framework.exception.core.ApiException;

public class IpSubnetMaskException extends ApiException {

	public IpSubnetMaskException(String param) {
		super("请检查ip网端/子网掩码配置后，重试:"+param);
	}
}

package codedriver.framework.exception.tenant;

import codedriver.framework.exception.core.ApiRuntimeException;

public class TenantNotFoundException extends ApiRuntimeException {

	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = 1099170784444348169L;

	public TenantNotFoundException(String tenant) {
		super("租户：" + tenant + "不存在");
	}
}

package neatlogic.framework.exception.tenant;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class TenantUnActiveException extends ApiRuntimeException {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1099170784444348169L;

	public TenantUnActiveException(String tenant) {
		super("租户：" + tenant + "已禁用");
	}
}

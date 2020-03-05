package codedriver.framework.exception.dashboard;

import codedriver.framework.exception.core.ApiRuntimeException;

public class DashboardHandlerNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 4778633677540696671L;

	public DashboardHandlerNotFoundException(String handler) {
		super("找不到类型为：" + handler + "的仪表板组件");
	}
}

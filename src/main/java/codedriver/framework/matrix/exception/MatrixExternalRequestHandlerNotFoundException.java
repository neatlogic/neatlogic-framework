package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class MatrixExternalRequestHandlerNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 8942293205532139273L;

	public MatrixExternalRequestHandlerNotFoundException(String handler) {
		super("找不到类型为：" + handler + "的外部数据源插件");
	}
}

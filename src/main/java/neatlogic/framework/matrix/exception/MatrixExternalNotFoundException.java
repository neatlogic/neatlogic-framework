package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixExternalNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 1708428607162097984L;

	public MatrixExternalNotFoundException(String name) {
		super(name + "的数据源关联的集成配置信息异常");
	}
}

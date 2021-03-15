package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiFieldValidRuntimeException;

public class MatrixExternalNotFoundException extends ApiFieldValidRuntimeException {

	private static final long serialVersionUID = 1708428607162097984L;

	public MatrixExternalNotFoundException(String name) {
		super(name + "的数据源关联的集成配置信息异常");
	}
}

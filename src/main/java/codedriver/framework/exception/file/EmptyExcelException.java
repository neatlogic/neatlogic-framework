package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

public class EmptyExcelException extends ApiRuntimeException {

	private static final long serialVersionUID = 4120489899073719516L;

	public EmptyExcelException() {
		super("Excel文件无内容");
	}

}

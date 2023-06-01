package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class EmptyExcelException extends ApiRuntimeException {

	private static final long serialVersionUID = 4120489899073719516L;

	public EmptyExcelException() {
		super("Excel文件无内容");
	}

}

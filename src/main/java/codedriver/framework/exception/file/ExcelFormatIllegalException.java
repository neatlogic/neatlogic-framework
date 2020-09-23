package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ExcelFormatIllegalException extends ApiRuntimeException {

	private static final long serialVersionUID = -1979153978468661777L;

	public ExcelFormatIllegalException(String format) {
		super("Excel文件格式错误，请上传" + format + "格式的Excel");
	}

}

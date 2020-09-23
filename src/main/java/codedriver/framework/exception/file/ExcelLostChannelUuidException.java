package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ExcelLostChannelUuidException extends ApiRuntimeException {

	private static final long serialVersionUID = -3954829429859732138L;

	public ExcelLostChannelUuidException() {
		super("Excel内没有找到服务UUID");
	}

}

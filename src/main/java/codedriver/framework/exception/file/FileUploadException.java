package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FileUploadException extends ApiRuntimeException {

	private static final long serialVersionUID = 2146739388736169580L;

	public FileUploadException(String msg) {
		super(msg);
	}

}

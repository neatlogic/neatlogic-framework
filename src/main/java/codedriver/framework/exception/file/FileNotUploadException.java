package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FileNotUploadException extends ApiRuntimeException {

	private static final long serialVersionUID = 2146739388736169580L;

	public FileNotUploadException() {
		super("没有上传文件");
	}

}

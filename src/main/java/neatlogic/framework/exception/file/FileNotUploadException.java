package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FileNotUploadException extends ApiRuntimeException {

	private static final long serialVersionUID = 2146739388736169580L;

	public FileNotUploadException() {
		super("exception.framework.filenotuploadexception");
	}

}

package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileExtNotAllowedException extends ApiRuntimeException {
	public FileExtNotAllowedException(String fileExt) {
		super(fileExt + "不是合法的附件类型");
	}

}

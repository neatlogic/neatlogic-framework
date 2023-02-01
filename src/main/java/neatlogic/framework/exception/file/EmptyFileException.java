package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class EmptyFileException extends ApiRuntimeException {
	public EmptyFileException() {
		super("请唔上传空白附件");
	}

}

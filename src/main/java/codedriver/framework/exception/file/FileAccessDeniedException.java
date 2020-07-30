package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileAccessDeniedException extends ApiRuntimeException {
	public FileAccessDeniedException(String filename,String operation) {
		super("您没有权限" + operation + "文件：" + filename);
	}

}

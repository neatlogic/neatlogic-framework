package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FilePathIllegalException extends ApiRuntimeException {
	public FilePathIllegalException(String filePath) {
		super("文件路径：" + filePath + "不合法");
	}

}

package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FilePathIllegalException extends ApiRuntimeException {
	public FilePathIllegalException(String filePath) {
		super("文件路径：" + filePath + "不合法");
	}

}

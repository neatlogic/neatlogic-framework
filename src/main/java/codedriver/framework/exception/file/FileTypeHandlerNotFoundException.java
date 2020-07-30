package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileTypeHandlerNotFoundException extends ApiRuntimeException {
	public FileTypeHandlerNotFoundException(String type) {
		super("附件类型：" + type + "找不到相应的控制器，请通知管理员或厂商解决");
	}

}

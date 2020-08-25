package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileStorageMediumHandlerNotFoundException extends ApiRuntimeException {
	public FileStorageMediumHandlerNotFoundException(String type) {
		super("存储介质类型：" + type + "找不到相应的控制器，请通知管理员或厂商解决");
	}

}

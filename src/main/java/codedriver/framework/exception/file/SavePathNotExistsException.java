package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class SavePathNotExistsException extends ApiRuntimeException {
	public SavePathNotExistsException(String belong) {
		super("附件归属：" + belong + "没有配置附件保存路径，请先配置");
	}

}

package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileTypeConfigNotFoundException extends ApiRuntimeException {
	public FileTypeConfigNotFoundException(String belong) {
		super("附件归属：" + belong + "没有任何配置，请先配置");
	}

}

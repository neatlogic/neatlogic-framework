package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileNotFoundException extends ApiRuntimeException {
	public FileNotFoundException(Long id) {
		super("附件：" + id + "不存在");
	}

}

package neatlogic.framework.exception.util;

import neatlogic.framework.exception.core.ApiException;

public class FreemarkerTransformException extends ApiException {
	private static final long serialVersionUID = 3623906073721231804L;

	public FreemarkerTransformException(String message) {
		super("模板转换失败，异常：" + message);
	}
}

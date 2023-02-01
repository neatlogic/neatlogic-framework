package neatlogic.framework.exception.util;

import neatlogic.framework.exception.core.ApiException;

public class FullIndexSliceWordException extends ApiException {

	public FullIndexSliceWordException(String message) {
		super("全文检索分词，异常：" + message);
	}
}

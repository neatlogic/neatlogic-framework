package codedriver.framework.exception.util;

import codedriver.framework.exception.core.ApiException;

public class FullIndexSliceWordException extends ApiException {

	public FullIndexSliceWordException(String message) {
		super("全文检索分词，异常：" + message);
	}
}

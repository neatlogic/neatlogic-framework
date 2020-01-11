package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class CurrentPageNumberOutOfBoundsException extends ApiRuntimeException {

	private static final long serialVersionUID = -5567628669798379860L;

	public CurrentPageNumberOutOfBoundsException(int currentPage, int pageCount) {
		super("当前页码不存在：currentPage: " + currentPage + ", pageCount: " + pageCount);
	}
}

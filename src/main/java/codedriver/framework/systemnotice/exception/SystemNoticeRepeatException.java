package codedriver.framework.systemnotice.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class SystemNoticeRepeatException extends ApiRuntimeException {

	private static final long serialVersionUID = -2920759826346310211L;

	public SystemNoticeRepeatException(String title) {
		super("系统公告：'" + title + "'已存在");
	}
}

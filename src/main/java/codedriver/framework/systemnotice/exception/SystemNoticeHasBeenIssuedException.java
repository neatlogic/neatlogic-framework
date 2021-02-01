package codedriver.framework.systemnotice.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class SystemNoticeHasBeenIssuedException extends ApiRuntimeException {

	private static final long serialVersionUID = 3061025905329230055L;

	public SystemNoticeHasBeenIssuedException(String title) {
		super("系统公告：'" + title + "'已下发，不可操作，请刷新系统公告管理页后查看公告状态");
	}
}

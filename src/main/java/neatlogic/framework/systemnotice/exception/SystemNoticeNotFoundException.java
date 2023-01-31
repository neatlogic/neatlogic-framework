package neatlogic.framework.systemnotice.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class SystemNoticeNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -7624698350730166349L;

	public SystemNoticeNotFoundException(Long id) {
		super("系统公告：" + id + "不存在");
	}
}

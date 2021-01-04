package codedriver.framework.notify.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NotifyJobNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 8236167326955410191L;

	public NotifyJobNotFoundException(Long id) {
		super("通知定时任务：'" + id + "'不存在");
	}
}

package codedriver.framework.heartbeat.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.asynchronization.thread.CodeDriverThread;

public class HeartbreakHandlerThread extends CodeDriverThread {
	private Logger logger = LoggerFactory.getLogger(HeartbreakHandlerThread.class);
	private IHeartbreakHandler observer;
	private Integer serverId;

	public HeartbreakHandlerThread(IHeartbreakHandler observer, Integer serverId) {
		this.observer = observer;
		this.serverId = serverId;
	}

	@Override
	protected void execute() {
		String oldThreadName = Thread.currentThread().getName();
		try {
			Thread.currentThread().setName("HEARTBREAK-HANDLER");
			observer.whenServerInactivated(serverId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			Thread.currentThread().setName(oldThreadName);
		}
	}

}

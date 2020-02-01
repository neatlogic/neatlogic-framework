package codedriver.framework.heartbeat.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.asynchronization.thread.CodeDriverThread;

public class HeartbeatObserverThread extends CodeDriverThread {
	private Logger logger = LoggerFactory.getLogger(HeartbeatObserverThread.class);
	private HeartbeatObserver observer;
	private Integer serverId;
	
	public HeartbeatObserverThread(HeartbeatObserver observer, Integer serverId) {
		this.observer = observer;
		this.serverId = serverId;
	}

	@Override
	protected void execute() {
		String oldThreadName = Thread.currentThread().getName();
		try {
			Thread.currentThread().setName("SERVER_OBSERVER_THREAD");
			observer.whenServerInactivated(serverId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			Thread.currentThread().setName(oldThreadName);
		}
	}

}

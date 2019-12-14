package codedriver.framework.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.asynchronization.thread.CodeDriverThread;

public class ServerObserverThread extends CodeDriverThread {
	private Logger logger = LoggerFactory.getLogger(ServerObserverThread.class);
	private ServerObserver observer;
	private Integer serverId;
	
	public ServerObserverThread(ServerObserver observer, Integer serverId) {
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

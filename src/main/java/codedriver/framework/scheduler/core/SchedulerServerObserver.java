package codedriver.framework.scheduler.core;

import org.springframework.beans.factory.annotation.Autowired;
import codedriver.framework.server.core.ServerObserver;

public class SchedulerServerObserver implements ServerObserver{

	@Autowired
	private SchedulerManager schedulerManager;
	
	@Override
	public void whenServerInactivated(Integer serverId) {
		schedulerManager.releaseLock(serverId);
		schedulerManager.loadNewJob();
	}

}

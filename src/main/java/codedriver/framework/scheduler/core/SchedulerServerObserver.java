package codedriver.framework.scheduler.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import codedriver.framework.heartbeat.core.HeartbeatObserver;

@Component
public class SchedulerServerObserver implements HeartbeatObserver{

	@Autowired
	private SchedulerManager schedulerManager;
	
	@Override
	public void whenServerInactivated(Integer serverId) {
		schedulerManager.releaseLock(serverId);
		schedulerManager.loadNewJob();
	}

}

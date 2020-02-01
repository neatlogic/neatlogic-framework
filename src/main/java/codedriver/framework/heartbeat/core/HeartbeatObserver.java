package codedriver.framework.heartbeat.core;

public interface HeartbeatObserver{
	
	public void whenServerInactivated(Integer serverId);
}

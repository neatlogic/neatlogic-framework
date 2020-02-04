package codedriver.framework.heartbeat.core;

public interface IHeartbreakHandler{
	
	public void whenServerInactivated(Integer serverId);
}

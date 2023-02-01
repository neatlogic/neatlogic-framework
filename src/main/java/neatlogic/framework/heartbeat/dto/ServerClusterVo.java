package neatlogic.framework.heartbeat.dto;

import neatlogic.framework.common.dto.BasePageVo;

public class ServerClusterVo extends BasePageVo{

	public final static String STARTUP = "startup";
	public final static String STOP = "stop";
	
	private String host;
	private Integer serverId;
	private String status;
	
	public ServerClusterVo() {
	}
	public ServerClusterVo(Integer serverId, String status) {
		this.serverId = serverId;
		this.status = status;
	}
	public ServerClusterVo(String host, Integer serverId, String status) {
		this.host = host;
		this.serverId = serverId;
		this.status = status;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getServerId() {
		return serverId;
	}
	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}

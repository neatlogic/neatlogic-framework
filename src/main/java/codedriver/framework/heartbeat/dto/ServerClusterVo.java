package codedriver.framework.heartbeat.dto;

import codedriver.framework.common.dto.BasePageVo;

public class ServerClusterVo extends BasePageVo{

	public final static String STARTUP = "startup";
	public final static String STOP = "stop";
	
	private String ip;
	private Integer port;
	private Integer serverId;
	private String status;
	
	public ServerClusterVo() {
	}
	public ServerClusterVo(Integer serverId, String status) {
		this.serverId = serverId;
		this.status = status;
	}
	public ServerClusterVo(String ip, Integer port, Integer serverId, String status) {
		this.ip = ip;
		this.port = port;
		this.serverId = serverId;
		this.status = status;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
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

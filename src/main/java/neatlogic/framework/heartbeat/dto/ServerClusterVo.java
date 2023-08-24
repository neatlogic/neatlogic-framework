package neatlogic.framework.heartbeat.dto;

import neatlogic.framework.common.dto.BaseEditorVo;

import java.util.Date;

public class ServerClusterVo extends BaseEditorVo {

	public final static String STARTUP = "startup";
	public final static String STOP = "stop";
	
	private String host;
	private Integer serverId;
	private String status;
	private Integer heartbeatRate;
	private Integer heartbeatThreshold;
	private Date heartbeatTime;

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

	public Integer getHeartbeatRate() {
		return heartbeatRate;
	}

	public void setHeartbeatRate(Integer heartbeatRate) {
		this.heartbeatRate = heartbeatRate;
	}

	public Integer getHeartbeatThreshold() {
		return heartbeatThreshold;
	}

	public void setHeartbeatThreshold(Integer heartbeatThreshold) {
		this.heartbeatThreshold = heartbeatThreshold;
	}

	public Date getHeartbeatTime() {
		return heartbeatTime;
	}

	public void setHeartbeatTime(Date heartbeatTime) {
		this.heartbeatTime = heartbeatTime;
	}
}

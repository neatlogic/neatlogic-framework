package codedriver.framework.restful.dto;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.dto.BasePageVo;

public class ApiAuditVo extends BasePageVo {

	public final static String SUCCEED = "succeed";
	public final static String FAILED = "failed";

	private String uuid;
	private String token;
	private String userId;
	private String authType;
	private Integer serverId;
	private String ip;
	private Date startTime;
	private Date endTime;
	private Long timeCost;
	private String status;
	private String param;
	private String error;
	private Object result;
	private String logPath;
	private transient String tenant;

	public ApiAuditVo() {
		this.setPageSize(20);
	}

	public synchronized String getUuid() {
		if (StringUtils.isBlank(uuid)) {
			uuid = UUID.randomUUID().toString().replace("-", "");
		}
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public Integer getServerId() {
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getTimeCost() {
		return timeCost;
	}

	public void setTimeCost(Long timeCost) {
		this.timeCost = timeCost;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}

package codedriver.framework.restful.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

public class ApiAuditVo extends BasePageVo {

	public final static String SUCCEED = "succeed";
	public final static String FAILED = "failed";

	@EntityField(name = "主键", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "地址", type = ApiParamType.STRING)
	private String token;
	@EntityField(name = "用户ID", type = ApiParamType.STRING)
	private String userUuid;
	@EntityField(name = "认证方式", type = ApiParamType.STRING)
	private String authtype;
	@EntityField(name = "服务器ID", type = ApiParamType.STRING)
	private Integer serverId;
	@EntityField(name = "用户IP", type = ApiParamType.STRING)
	private String ip;
	@EntityField(name = "开始时间", type = ApiParamType.LONG)
	private Date startTime;
	@EntityField(name = "结束时间", type = ApiParamType.LONG)
	private Date endTime;
	@EntityField(name = "耗时", type = ApiParamType.LONG)
	private Long timeCost;
	@EntityField(name = "状态", type = ApiParamType.STRING)
	private String status;
	@EntityField(name = "参数内容", type = ApiParamType.STRING)
	private String param;
	@EntityField(name = "异常内容", type = ApiParamType.STRING)
	private String error;
	@EntityField(name = "结果内容", type = ApiParamType.STRING)
	private Object result;

	@JSONField(serialize = false)
	private String paramHash;
	@JSONField(serialize = false)
	private String errorHash;
	@JSONField(serialize = false)
	private String resultHash;
	private transient String logPath;
	private transient String tenant;

	public ApiAuditVo() {
		this.setPageSize(20);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getAuthtype() {
		return authtype;
	}

	public void setAuthtype(String authtype) {
		this.authtype = authtype;
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

	public Long getId() {
		if (id == null) {
			id = SnowflakeUtil.uniqueLong();
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getParamHash() {
		if (StringUtils.isBlank(paramHash) && StringUtils.isNotBlank(param)) {
			paramHash = DigestUtils.md5DigestAsHex(param.getBytes());
		}
		return paramHash;
	}

	public void setParamHash(String paramHash) {
		this.paramHash = paramHash;
	}

	public String getErrorHash() {
		if (StringUtils.isBlank(errorHash) && StringUtils.isNotBlank(error)) {
			errorHash = DigestUtils.md5DigestAsHex(error.getBytes());
		}
		return errorHash;
	}

	public void setErrorHash(String errorHash) {
		this.errorHash = errorHash;
	}

	public String getResultHash() {
		if (StringUtils.isBlank(resultHash) && result != null) {
			resultHash = DigestUtils.md5DigestAsHex(JSON.toJSONString(result).getBytes());
		}
		return resultHash;
	}

	public void setResultHash(String resultHash) {
		this.resultHash = resultHash;
	}

}

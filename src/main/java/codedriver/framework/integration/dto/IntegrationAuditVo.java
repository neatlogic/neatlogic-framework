package codedriver.framework.integration.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

public class IntegrationAuditVo extends BasePageVo {
	@EntityField(name = "id", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "集成配置uuid", type = ApiParamType.STRING)
	private String integrationUuid;
	@EntityField(name = "用户uuid", type = ApiParamType.STRING)
	private String userUuid;
	@EntityField(name = "用户名", type = ApiParamType.STRING)
	private String userName;
	@EntityField(name = "请求来源", type = ApiParamType.STRING)
	private String requestFrom;
	@JSONField(serialize = false)
	private transient Integer serverId;
	@EntityField(name = "开始时间", type = ApiParamType.LONG)
	private Date startTime;
	@EntityField(name = "结束时间", type = ApiParamType.LONG)
	private Date endTime;
	@EntityField(name = "耗时（毫秒）", type = ApiParamType.LONG)
	private Long timeCost;
	@EntityField(name = "状态", type = ApiParamType.STRING)
	private String status;
	@EntityField(name = "参数内容hash", type = ApiParamType.STRING)
	private String paramHash;
	@EntityField(name = "结果内容hash", type = ApiParamType.STRING)
	private String resultHash;
	@EntityField(name = "错误内容hash", type = ApiParamType.STRING)
	private String errorHash;
	@EntityField(name = "请求参数", type = ApiParamType.STRING)
	private String param;
	@EntityField(name = "返回结果", type = ApiParamType.STRING)
	private String result;
	@EntityField(name = "异常", type = ApiParamType.STRING)
	private String error;

	public Long getId() {
		if (id == null) {
			id = SnowflakeUtil.uniqueLong();
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIntegrationUuid() {
		return integrationUuid;
	}

	public void setIntegrationUuid(String integrationUuid) {
		this.integrationUuid = integrationUuid;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public Integer getServerId() {
		if (serverId == null) {
			serverId = Config.SCHEDULE_SERVER_ID;
		}
		return serverId;
	}

	public void setServerId(Integer serverId) {
		this.serverId = serverId;
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
		if (timeCost == null) {
			if (startTime != null && endTime != null) {
				timeCost = endTime.getTime() - startTime.getTime();
			}
		}
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

	public String getParamHash() {
		if (StringUtils.isBlank(paramHash) && StringUtils.isNotBlank(param)) {
			paramHash = DigestUtils.md5DigestAsHex(param.getBytes());
		}
		return paramHash;
	}

	public void setParamHash(String paramHash) {
		this.paramHash = paramHash;
	}

	public String getResultHash() {
		if (StringUtils.isBlank(resultHash) && StringUtils.isNotBlank(result)) {
			resultHash = DigestUtils.md5DigestAsHex(result.getBytes());
		}
		return resultHash;
	}

	public void setResultHash(String resultHash) {
		this.resultHash = resultHash;
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

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void appendError(String error) {
		if (StringUtils.isNotBlank(error)) {
			if (StringUtils.isNotBlank(this.error)) {
				this.error += "\n" + error;
			} else {
				this.error = error;
			}
		}
	}

	public String getRequestFrom() {
		return requestFrom;
	}

	public void setRequestFrom(String requestFrom) {
		this.requestFrom = requestFrom;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}

package codedriver.framework.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

public class TenantAuditVo extends BasePageVo {
	public enum Status {
		RUNNING("running", "运行中"), SUCCEED("succeed", "已成功"), FAILED("failed", "已失败");

		private String status;
		private String text;

		private Status(String _status, String _text) {
			this.status = _status;
			this.text = _text;
		}

		public static String getValue(String _status) {
			for (Status s : Status.values()) {
				if (s.getValue().equals(_status)) {
					return s.getValue();
				}
			}
			return null;
		}

		public static String getText(String name) {
			for (Status s : Status.values()) {
				if (s.getValue().equals(name)) {
					return s.getText();
				}
			}
			return "";
		}

		public String getValue() {
			return status;
		}

		public String getText() {
			return text;
		}
	}

	@EntityField(name = "id", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "租户uuid", type = ApiParamType.STRING)
	private String tenantUuid;
	@EntityField(name = "模块id", type = ApiParamType.STRING)
	private String moduleId;
	@EntityField(name = "版本序号", type = ApiParamType.INTEGER)
	private int version;
	@EntityField(name = "执行日志路径", type = ApiParamType.STRING)
	private String logPath;
	@EntityField(name = "错误日志路径", type = ApiParamType.STRING)
	private String errPath;
	@EntityField(name = "开始时间", type = ApiParamType.LONG)
	private Date startTime;
	@EntityField(name = "结束时间", type = ApiParamType.LONG)
	private Date endTime;
	@EntityField(name = "状态", type = ApiParamType.STRING)
	private String status;
	@EntityField(name = "result", type = ApiParamType.STRING)
	private String result;
	@EntityField(name = "error", type = ApiParamType.STRING)
	private String error;
	@JSONField(serialize = false)
	private transient String resultHash;
	@JSONField(serialize = false)
	private transient String errorHash;

	public String getTenantUuid() {
		return tenantUuid;
	}

	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getErrPath() {
		return errPath;
	}

	public void setErrPath(String errPath) {
		this.errPath = errPath;
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

}

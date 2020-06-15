package codedriver.framework.dto;

import java.util.Date;

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
	@EntityField(name = "日志路径", type = ApiParamType.STRING)
	private String logPath;
	@EntityField(name = "开始时间", type = ApiParamType.LONG)
	private Date startTime;
	@EntityField(name = "结束时间", type = ApiParamType.LONG)
	private Date endTime;
	@EntityField(name = "状态", type = ApiParamType.STRING)
	private String status;

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

}

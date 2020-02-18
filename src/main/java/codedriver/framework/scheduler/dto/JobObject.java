package codedriver.framework.scheduler.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JobObject implements Serializable {

	private static final long serialVersionUID = -8409651508383155447L;
	private String jobName;
	private String jobGroup;
	private String cron;
	private Date beginTime;
	private Date endTime;
	private String type;
	private String jobHandler;
	private Integer needAudit;
	private String tenantUuid;
	private Integer intervalInSeconds;
	private Map<String, Object> dataMap;

	private JobObject(Builder builder) {
		this.jobName = builder.jobId;
		this.jobGroup = builder.jobGroup;
		this.jobHandler = builder.jobHandler;
		this.cron = builder.cron;
		this.beginTime = builder.beginTime;
		this.endTime = builder.endTime;
		this.needAudit = builder.needAudit;
		this.tenantUuid = builder.tenantUuid;
		this.type = builder.type;
		this.intervalInSeconds = builder.intervalInSeconds;
		this.dataMap = builder.dataMap;
	}

	private JobObject() {

	}

	public String getJobName() {
		return jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public String getCron() {
		return cron;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getJobHandler() {
		return jobHandler;
	}

	public Integer getNeedAudit() {
		return needAudit;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public String getTenantUuid() {
		return tenantUuid;
	}

	public String getType() {
		return type;
	}

	public static class Builder {
		// 必要参数
		private final String jobId;
		private final String jobGroup;
		private final String jobHandler;
		private final String tenantUuid;

		// 可选参数
		private String cron;
		private Date beginTime;
		private Date endTime;
		private Integer needAudit = 0;
		private String type = "private";
		private Integer intervalInSeconds;
		private Map<String, Object> dataMap;

		public Builder(String jobId, String jobGroup, String jobHandler, String tenantUuid) {
			this.jobId = jobId;
			this.jobGroup = jobGroup;
			this.jobHandler = jobHandler;
			this.tenantUuid = tenantUuid;
		}

		public Builder withCron(String _cron) {
			cron = _cron;
			return this;
		}

		public Builder withBeginTime(Date _beginTime) {
			beginTime = _beginTime;
			return this;
		}

		public Builder withEndTime(Date _endTime) {
			endTime = _endTime;
			return this;
		}

		public Builder setType(String _type) {
			type = _type;
			return this;
		}

		public Builder needAudit(Integer _needAudit) {
			needAudit = _needAudit;
			return this;
		}

		public Builder withIntervalInSeconds(Integer _intervalInSeconds) {
			intervalInSeconds = _intervalInSeconds;
			return this;
		}

		public Builder addData(String key, Object data) {
			if (dataMap == null) {
				dataMap = new HashMap<>();
			}
			dataMap.put(key, data);
			return this;
		}

		public JobObject build() {
			return new JobObject(this);
		}
	}

	public Object getData(String key) {
		if (dataMap != null) {
			return dataMap.get(key);
		}
		return null;
	}

	public Integer getIntervalInSeconds() {
		return intervalInSeconds;
	}

}

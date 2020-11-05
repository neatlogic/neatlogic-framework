package codedriver.framework.scheduler.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

public class JobAuditVo extends BasePageVo {

	public final static String RUNNING = "running";

	public final static String SUCCEED = "succeed";

	public final static String FAILED = "failed";
	@EntityField(name = "记录id", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "定时作业uuid", type = ApiParamType.STRING)
	private String jobUuid;
	@EntityField(name = "开始时间", type = ApiParamType.LONG)
	private Date startTime;
	@EntityField(name = "结束时间", type = ApiParamType.LONG)
	private Date endTime;
	@EntityField(name = "日志内容", type = ApiParamType.STRING)
	private String content;
	private String contentHash;
	@EntityField(name = "执行状态(succeed：成功；failed:失败；running：进行中)", type = ApiParamType.STRING)
	private String status = RUNNING;
	@EntityField(name = "服务器id", type = ApiParamType.INTEGER)
	private Integer serverId;
	@EntityField(name = "日志内容长度", type = ApiParamType.INTEGER)
	private int contentLength;

	public JobAuditVo() {
		this.setPageSize(20);
	}

	public JobAuditVo(String _jobUUid, Integer _serverId) {
		this.jobUuid = _jobUUid;
		this.serverId = _serverId;
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

	public String getJobUuid() {
		return jobUuid;
	}

	public void setJobUuid(String jobUuid) {
		this.jobUuid = jobUuid;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void appendContent(String content) {
		if (StringUtils.isNotBlank(content)) {
			if (this.content == null) {
				this.content = "";
			}
			this.content += content;
		}
	}

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public String getContentHash() {
		if (StringUtils.isBlank(contentHash) && StringUtils.isNotBlank(content)) {
			contentHash = DigestUtils.md5DigestAsHex(content.getBytes());
		}
		return contentHash;
	}

	public void setContentHash(String contentHash) {
		this.contentHash = contentHash;
	}
}

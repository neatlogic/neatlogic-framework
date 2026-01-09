package neatlogic.framework.scheduler.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.crossover.CrossoverServiceFactory;
import neatlogic.framework.crossover.IScheduleCrossoverService;
import neatlogic.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JobStatusVo extends BaseEditorVo {
	private static final long serialVersionUID = 2313591895745650036L;
	@EntityField(name = "定时作业名称",
			type = ApiParamType.STRING)
	private String jobName;
	@EntityField(name = "定时作业组名",
			type = ApiParamType.STRING)
	private String jobGroup;
	@EntityField(name = "定时作业类名",
			type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "下一次被唤醒时间",
			type = ApiParamType.LONG)
	private Date nextFireTime;
	@EntityField(name = "最后一次被唤醒时间",
			type = ApiParamType.LONG)
	private Date lastFireTime;
	@EntityField(name = "最后一次完成时间",
			type = ApiParamType.LONG)
	private Date lastFinishTime;
	@EntityField(name = "执行次数",
			type = ApiParamType.INTEGER)
	private Integer execCount = 0;
	@EntityField(name = "是否已加载",
			type = ApiParamType.INTEGER)
	private Integer isLoad;

	private List<JobLoadVo> jobLoadList;

	@JSONField(serialize = false)
	private Integer needAudit;

	public JobStatusVo() {
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public Date getLastFireTime() {
		return lastFireTime;
	}

	public void setLastFireTime(Date lastFireTime) {
		this.lastFireTime = lastFireTime;
	}

	public Date getLastFinishTime() {
		return lastFinishTime;
	}

	public void setLastFinishTime(Date lastFinishTime) {
		this.lastFinishTime = lastFinishTime;
	}

	public Integer getExecCount() {
		return execCount;
	}

	public void setExecCount(Integer execCount) {
		this.execCount = execCount;
	}

	public Integer getNeedAudit() {
		return needAudit;
	}

	public void setNeedAudit(Integer needAudit) {
		this.needAudit = needAudit;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public Integer getIsLoad() {
		if (isLoad == null) {
			isLoad = 0;
			List<JobLoadVo> loadList = this.getJobLoadList();
			if (CollectionUtils.isNotEmpty(loadList)) {
				for (JobLoadVo jobLoadVo : loadList) {
					if (Objects.equals(jobLoadVo.getIsLoad(), 1)) {
						isLoad = 1;
						break;
					}
				}
			}
		}
		return isLoad;
	}

	public void setIsLoad(Integer isLoad) {
		this.isLoad = isLoad;
	}

	public List<JobLoadVo> getJobLoadList() {
		if (jobLoadList == null) {
			if (StringUtils.isNotBlank(this.jobName) && StringUtils.isNotBlank(this.jobGroup)) {
				IScheduleCrossoverService scheduleCrossoverService = CrossoverServiceFactory.getApi(IScheduleCrossoverService.class);
				jobLoadList = scheduleCrossoverService.getJobLoadList(this.jobName, this.jobGroup);
			}
		}
		return jobLoadList;
	}

	public void setJobLoadList(List<JobLoadVo> jobLoadList) {
		this.jobLoadList = jobLoadList;
	}
}

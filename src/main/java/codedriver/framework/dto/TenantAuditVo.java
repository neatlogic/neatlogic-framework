package codedriver.framework.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

public class TenantAuditVo extends BasePageVo {
	public enum Status {
		DOING, DONE;
	}

	@EntityField(name = "id", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "分组id", type = ApiParamType.LONG)
	private Long groupId;
	@EntityField(name = "租户id", type = ApiParamType.LONG)
	private Long tenantId;
	@EntityField(name = "模块id", type = ApiParamType.STRING)
	private String moduleId;
	@EntityField(name = "模块名称", type = ApiParamType.STRING)
	private String moduleName;
	@EntityField(name = "模块分组", type = ApiParamType.STRING)
	private String moduleGroup;
	@EntityField(name = "模块分组名称", type = ApiParamType.STRING)
	private String moduleGroupName;
	@EntityField(name = "版本序号", type = ApiParamType.STRING)
	private String version;
	@EntityField(name = "开始时间", type = ApiParamType.LONG)
	private Date startTime;
	@EntityField(name = "结束时间", type = ApiParamType.LONG)
	private Date endTime;
	@EntityField(name = "耗时（毫秒）", type = ApiParamType.LONG)
	private Long timeCost;
	@EntityField(name = "状态", type = ApiParamType.STRING)
	private String status;
	@EntityField(name = "日志内容", type = ApiParamType.STRING)
	private String result;
	@EntityField(name = "异常内容", type = ApiParamType.STRING)
	private String error;
	@EntityField(name = "日志内容hash", type = ApiParamType.STRING)
	private String resultHash;
	@EntityField(name = "异常内容hash", type = ApiParamType.STRING)
	private String errorHash;

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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Long getTimeCost() {
		return timeCost;
	}

	public void setTimeCost(Long timeCost) {
		this.timeCost = timeCost;
	}

	public String getModuleGroup() {
		return moduleGroup;
	}

	public void setModuleGroup(String moduleGroup) {
		this.moduleGroup = moduleGroup;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getModuleName() {
		if (StringUtils.isNotBlank(this.moduleId)) {
			ModuleVo moduleVo = ModuleUtil.getModuleById(this.moduleId);
			if (moduleVo != null) {
				moduleName = moduleVo.getName();
			}
		}
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleGroupName() {
		if (StringUtils.isNotBlank(this.moduleGroup)) {
			ModuleGroupVo moduleGroupVo = ModuleUtil.getModuleGroup(this.moduleGroup);
			if (moduleGroupVo != null) {
				this.moduleGroupName = moduleGroupVo.getGroupName();
			}
		}
		return moduleGroupName;
	}

	public void setModuleGroupName(String moduleGroupName) {
		this.moduleGroupName = moduleGroupName;
	}

}

package codedriver.framework.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

import java.util.Date;
import java.util.List;

public class TenantVo extends BasePageVo {
	public enum Status {
		BUILDING, BUILDED;
	}

	@EntityField(name = "id", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "uuid", type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "是否激活", type = ApiParamType.INTEGER)
	private Integer isActive;
	@EntityField(name = "描述", type = ApiParamType.STRING)
	private String description;
	@EntityField(name = "有效期限", type = ApiParamType.LONG)
	private Date expireDate;
	@EntityField(name = "激活模块", type = ApiParamType.JSONARRAY)
	private List<ModuleVo> moduleList;
	@EntityField(name = "激活模块分组", type = ApiParamType.JSONARRAY)
	private List<ModuleGroupVo> moduleGroupList;
	@EntityField(name = "状态", type = ApiParamType.STRING)
	private String status;

	public TenantVo() {
		this.setPageSize(20);
	}

	public TenantVo(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public List<ModuleVo> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<ModuleVo> moduleList) {
		this.moduleList = moduleList;
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

	public List<ModuleGroupVo> getModuleGroupList() {
		return moduleGroupList;
	}

	public void setModuleGroupList(List<ModuleGroupVo> moduleGroupList) {
		this.moduleGroupList = moduleGroupList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}

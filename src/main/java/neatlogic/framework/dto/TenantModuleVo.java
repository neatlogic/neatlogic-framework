package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.Date;

public class TenantModuleVo {

	@EntityField(name = "租户id", type = ApiParamType.LONG)
	private Long tenantId;
	@EntityField(name = "模块id", type = ApiParamType.STRING)
	private String moduleId;
	@EntityField(name = "ddl执行状态,1:成功 0:失败", type = ApiParamType.INTEGER)
	private Integer ddlStatus;
	@EntityField(name = "dml执行状态,1:成功 0:失败", type = ApiParamType.INTEGER)
	private Integer dmlStatus;
	@EntityField(name = "添加日期", type = ApiParamType.LONG)
	private Date fcd;
	@EntityField(name = "更新日期", type = ApiParamType.LONG)
	private Date lcd;

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public Date getFcd() {
		return fcd;
	}

	public void setFcd(Date fcd) {
		this.fcd = fcd;
	}

	public Date getLcd() {
		return lcd;
	}

	public void setLcd(Date lcd) {
		this.lcd = lcd;
	}

	public Integer getDdlStatus() {
		return ddlStatus;
	}

	public void setDdlStatus(Integer ddlStatus) {
		this.ddlStatus = ddlStatus;
	}

	public Integer getDmlStatus() {
		return dmlStatus;
	}

	public void setDmlStatus(Integer dmlStatus) {
		this.dmlStatus = dmlStatus;
	}
}

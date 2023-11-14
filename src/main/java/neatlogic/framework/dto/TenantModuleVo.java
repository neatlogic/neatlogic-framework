package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.Date;

public class TenantModuleVo {

	@EntityField(name = "common.tenantuuid", type = ApiParamType.LONG)
	private String tenantUuid;
	@EntityField(name = "term.cmdb.moduleid", type = ApiParamType.STRING)
	private String moduleId;
	@EntityField(name = "nfd.tenantmodulevo.ddlstatus", type = ApiParamType.INTEGER)
	private Integer ddlStatus;
	@EntityField(name = "nfd.tenantmodulevo.dmlstatus", type = ApiParamType.INTEGER)
	private Integer dmlStatus;
	@EntityField(name = "common.createdate", type = ApiParamType.LONG)
	private Date fcd;
	@EntityField(name = "common.editdate", type = ApiParamType.LONG)
	private Date lcd;
	@EntityField(name = "term.deploy.version", type = ApiParamType.STRING)
	private String version;

	public String getTenantUuid() {
		return tenantUuid;
	}

	public void setTenantUuid(String tenantUuid) {
		this.tenantUuid = tenantUuid;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}

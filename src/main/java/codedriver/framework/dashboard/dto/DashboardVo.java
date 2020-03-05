package codedriver.framework.dashboard.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class DashboardVo {
	@EntityField(name = "仪表板uuid", type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "仪表板名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "是否激活", type = ApiParamType.INTEGER)
	private int isActive;
	@EntityField(name = "仪表板创建时间", type = ApiParamType.LONG)
	private Date fcd;
	@EntityField(name = "仪表板创建人", type = ApiParamType.STRING)
	private String fcu;
	@EntityField(name = "仪表板修改时间", type = ApiParamType.LONG)
	private Date lcd;
	@EntityField(name = "仪表板修改人", type = ApiParamType.STRING)
	private String lcu;
	@EntityField(name = "仪表板组件列表", type = ApiParamType.JSONOBJECT)
	private List<DashboardWidgetVo> widgetList;

	public String getUuid() {
		if (StringUtils.isBlank(uuid)) {
			uuid = UUID.randomUUID().toString().replace("-", "");
		}
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

	public Date getFcd() {
		return fcd;
	}

	public void setFcd(Date fcd) {
		this.fcd = fcd;
	}

	public String getFcu() {
		return fcu;
	}

	public void setFcu(String fcu) {
		this.fcu = fcu;
	}

	public Date getLcd() {
		return lcd;
	}

	public void setLcd(Date lcd) {
		this.lcd = lcd;
	}

	public String getLcu() {
		return lcu;
	}

	public void setLcu(String lcu) {
		this.lcu = lcu;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public List<DashboardWidgetVo> getWidgetList() {
		return widgetList;
	}

	public void setWidgetList(List<DashboardWidgetVo> widgetList) {
		this.widgetList = widgetList;
	}
}

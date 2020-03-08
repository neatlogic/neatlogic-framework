package codedriver.framework.dashboard.dto;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class DashboardWidgetVo {
	@EntityField(name = "组件uuid", type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "组件名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "组件定时刷新间隔，单位：秒，为0代表不定时刷新", type = ApiParamType.INTEGER)
	private int refreshInterval;
	@EntityField(name = "组件说明", type = ApiParamType.STRING)
	private String description;
	@EntityField(name = "组件处理类", type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "组件图表类型", type = ApiParamType.STRING)
	private String chartType;
	private transient String conditionConfig;
	private transient String chartConfig;
	private transient JSONObject conditionConfigObj;
	private transient JSONObject chartConfigObj;
	@EntityField(name = "明细组件uuid", type = ApiParamType.STRING)
	private String detailWidgetUuid;
	@EntityField(name = "组件位置信息", type = ApiParamType.STRING)
	private String position;
	@EntityField(name = "组件创建时间", type = ApiParamType.LONG)
	private Date fcd;
	@EntityField(name = "组件创建人", type = ApiParamType.STRING)
	private String fcu;
	@EntityField(name = "组件修改时间", type = ApiParamType.LONG)
	private Date lcd;
	@EntityField(name = "组件修改人", type = ApiParamType.STRING)
	private String lcu;

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

	public int getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getConditionConfig() {
		return conditionConfig;
	}

	public void setConditionConfig(String conditionConfig) {
		this.conditionConfig = conditionConfig;
	}

	public String getChartConfig() {
		return chartConfig;
	}

	public void setChartConfig(String chartConfig) {
		this.chartConfig = chartConfig;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
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

	public JSONObject getConditionConfigObj() {
		if (conditionConfigObj == null && StringUtils.isNotBlank(conditionConfig)) {
			try {
				conditionConfigObj = JSONObject.parseObject(conditionConfig);
			} catch (Exception ex) {

			}
		}
		return conditionConfigObj;
	}

	public void setConditionConfigObj(JSONObject conditionConfigObj) {
		this.conditionConfigObj = conditionConfigObj;
	}

	public JSONObject getChartConfigObj() {
		if (chartConfigObj == null && StringUtils.isNotBlank(chartConfig)) {
			try {
				chartConfigObj = JSONObject.parseObject(chartConfig);
			} catch (Exception ex) {

			}
		}
		return chartConfigObj;
	}

	public void setChartConfigObj(JSONObject chartConfigObj) {
		this.chartConfigObj = chartConfigObj;
	}

	public String getDetailWidgetUuid() {
		return detailWidgetUuid;
	}

	public void setDetailWidgetUuid(String detailWidgetUuid) {
		this.detailWidgetUuid = detailWidgetUuid;
	}
}

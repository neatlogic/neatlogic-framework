package codedriver.framework.dashboard.dto;

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
	@EntityField(name = "仪表板uuid", type = ApiParamType.STRING)
	private String dashboardUuid;
	private transient String conditionConfig;
	private transient String chartConfig;
	private transient JSONObject conditionConfigObj;
	private JSONObject chartConfigObj;
	@EntityField(name = "明细组件uuid", type = ApiParamType.STRING)
	private String detailWidgetUuid;
	@EntityField(name = "x坐标", type = ApiParamType.INTEGER)
	private int x;
	@EntityField(name = "y坐标", type = ApiParamType.INTEGER)
	private int y;
	@EntityField(name = "索引", type = ApiParamType.INTEGER)
	private int i;
	@EntityField(name = "高度", type = ApiParamType.INTEGER)
	private int h;
	@EntityField(name = "宽度", type = ApiParamType.INTEGER)
	private int w;

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

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public String getDashboardUuid() {
		return dashboardUuid;
	}

	public void setDashboardUuid(String dashboardUuid) {
		this.dashboardUuid = dashboardUuid;
	}
}

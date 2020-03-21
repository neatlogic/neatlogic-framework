package codedriver.framework.dashboard.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.dto.ChartDataVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;

public abstract class DashboardHandlerBase implements IDashboardHandler {
	public final ChartDataVo getData(DashboardWidgetVo widgetVo) {
		JSONObject chartConfigObj = widgetVo.getChartConfigObj();
		JSONArray dataList = myGetData(widgetVo);
		
		ChartDataVo chartDataVo = new ChartDataVo();
		chartDataVo.setGroupField(chartConfigObj.getString("groupField"));
		chartDataVo.setSubGroupField(chartConfigObj.getString("subGroupField"));
		chartDataVo.setValueField(chartConfigObj.getString("valueField"));
		chartDataVo.setDataList(dataList);
		
		chartDataVo.setConfigObj(widgetVo.getChartConfigObj());
		return chartDataVo;
	}

	protected abstract JSONArray myGetData(DashboardWidgetVo widgetVo);
}

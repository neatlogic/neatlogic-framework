package codedriver.framework.dashboard.core;

import com.alibaba.fastjson.JSONArray;

import codedriver.framework.dashboard.dto.ChartDataVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;

public abstract class DashboardHandlerBase implements IDashboardHandler {
	public final ChartDataVo getData(DashboardWidgetVo widgetVo) {
		JSONArray dataList = myGetData(widgetVo);
		ChartDataVo chartDataVo = new ChartDataVo();
		chartDataVo.setDataList(dataList);
		chartDataVo.setConfigObj(widgetVo.getChartConfigObj());
		return chartDataVo;
	}

	protected abstract JSONArray myGetData(DashboardWidgetVo widgetVo);
}

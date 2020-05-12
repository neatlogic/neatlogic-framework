package codedriver.framework.dashboard.core;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.dto.ChartDataVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;

public abstract class DashboardHandlerBase implements IDashboardHandler {
	public final ChartDataVo getData(DashboardWidgetVo widgetVo) {
		ChartDataVo chartDataVo = new ChartDataVo();
		chartDataVo.setData(myGetData(widgetVo));
		chartDataVo.setConfigObj(widgetVo.getChartConfigObj());
		return chartDataVo;
	}

	protected abstract JSONObject myGetData(DashboardWidgetVo widgetVo);
}

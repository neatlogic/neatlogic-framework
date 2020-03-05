package codedriver.framework.dashboard.core;

import codedriver.framework.dashboard.dto.ChartDataVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;

public abstract class DashboardHandlerBase implements IDashboardHandler {
	public final ChartDataVo getData(DashboardWidgetVo widgetVo) {
		ChartDataVo data = myGetData(widgetVo);
		data.setConfigObj(widgetVo.getChartConfigObj());
		return data;
	}

	protected abstract ChartDataVo myGetData(DashboardWidgetVo widgetVo);
}

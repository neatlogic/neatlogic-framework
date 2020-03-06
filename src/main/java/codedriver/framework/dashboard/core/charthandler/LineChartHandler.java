package codedriver.framework.dashboard.core.charthandler;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.dto.DashboardWidgetVo;
import codedriver.framework.dashboard.dto.ChartDataVo;

public interface LineChartHandler {
	public default JSONObject getLineData(DashboardWidgetVo datasourceWidgetVo) {
		if (datasourceWidgetVo != null) {

		}
		return null;
	}

	public List<ChartDataVo> myGetLineData();
}

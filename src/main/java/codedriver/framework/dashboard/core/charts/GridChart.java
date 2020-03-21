package codedriver.framework.dashboard.core.charts;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.core.DashboardChartBase;

public class GridChart extends DashboardChartBase {

	@Override
	public String getName() {
		return "gridchart";
	}

	@Override
	public JSONArray getData(JSONArray dataList, JSONObject configObj) {
		return null;
	}

}

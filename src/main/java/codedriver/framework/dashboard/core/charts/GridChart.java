package codedriver.framework.dashboard.core.charts;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.core.DashboardChartBase;

public class GridChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] { "gridchart" };
	}


	@Override
	public JSONObject getChartConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONArray getData(JSONObject dataMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getDataMap(JSONArray nextDataList, JSONObject configObj, JSONObject preDatas) {
		// TODO Auto-generated method stub
		return null;
	}

}

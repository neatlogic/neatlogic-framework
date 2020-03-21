package codedriver.framework.dashboard.core.charts;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.core.DashboardChartBase;

public class PieChart extends DashboardChartBase {

	@Override
	public String getName() {
		return "piechart";
	}

	@Override
	public JSONArray getData(JSONArray dataList, JSONObject configObj) {
		return null;
	}

}

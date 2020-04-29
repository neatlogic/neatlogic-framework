package codedriver.framework.dashboard.core.charts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.core.DashboardChartBase;

public class PieChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] { "piechart" };
	}

	@Override
	public JSONArray getData(JSONArray dataList, JSONObject configObj) {
		String groupField = configObj.getString("groupField");
		String aggregate = configObj.getString("aggregate");
		Map<String, Double> resultMap = new HashMap<>();
		if (aggregate.equals("count")) {
			for (int i = 0; i < dataList.size(); i++) {
				JSONObject data = dataList.getJSONObject(i);
				String group = data.getString(groupField);
				if(StringUtils.isNotBlank(group)){
					if (!resultMap.containsKey(group)) {
						resultMap.put(group, 1D);
					} else {
						resultMap.put(group, resultMap.get(group) + 1D);
					}
				}
			}
		} 

		if (MapUtils.isNotEmpty(resultMap)) {
			Iterator<String> itKey = resultMap.keySet().iterator();
			JSONArray returnList = new JSONArray();
			while (itKey.hasNext()) {
				String key = itKey.next();
				JSONObject data = new JSONObject();
				data.put("column", key);
				data.put("num", resultMap.get(key));
				returnList.add(data);
			}
			return returnList;
		}
		return null;
	}

	@Override
	public JSONObject getChartConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}

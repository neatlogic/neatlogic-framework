package codedriver.framework.dashboard.core.charts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

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
		String valueField = configObj.getString("valueField");
		String groupField = configObj.getString("groupField");
		String subGroupField = configObj.getString("subGroupField");
		String aggregate = configObj.getString("aggregate");
		Map<String, Double> resultMap = new HashMap<>();
		if (aggregate.equals("count")) {
			for (int i = 0; i < dataList.size(); i++) {
				JSONObject data = dataList.getJSONObject(i);
				String group = data.getString(groupField);
				if (StringUtils.isNotBlank(data.getString(subGroupField))) {
					group += "#" + data.getString(subGroupField);
				}
				if (!resultMap.containsKey(group)) {
					resultMap.put(group, 1D);
				} else {
					resultMap.put(group, resultMap.get(group) + 1D);
				}
			}
		} else if (aggregate.equals("sum")) {
			for (int i = 0; i < dataList.size(); i++) {
				JSONObject data = dataList.getJSONObject(i);
				String group = data.getString(groupField);
				if (StringUtils.isNotBlank(data.getString(subGroupField))) {
					group += "#" + data.getString(subGroupField);
				}
				if (!resultMap.containsKey(group)) {
					resultMap.put(group, StringUtils.isNumeric(data.getString(valueField)) ? data.getDouble(valueField) : 0D);
				} else {
					if (StringUtils.isNumeric(data.getString(valueField))) {
						resultMap.put(group, resultMap.get(group) + data.getDouble(valueField));
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
				if (key.indexOf("#") > -1) {
					data.put(groupField, key.split("#")[0]);
					data.put(subGroupField, key.split("#")[1]);
				} else {
					data.put(groupField, key);
				}
				data.put(valueField, resultMap.get(key));
				returnList.add(data);
			}
			return returnList;
		}
		return null;
	}

}

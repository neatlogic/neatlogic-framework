package codedriver.framework.dashboard.core.charts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.core.DashboardChartBase;
import codedriver.framework.exception.dashboard.DashboardFieldNotFoundException;

public class BarChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] { "barchart" };
	}

	@Override
	public JSONArray getData(JSONArray dataList, JSONObject configObj) {
		String groupField = configObj.getString("groupField");
		String subGroupField = configObj.getString("subGroupField");
		String aggregate = configObj.getString("aggregate");
		String subGroup = StringUtils.EMPTY;
		Map<String, Double> resultMap = new HashMap<>();
		Map<String, String> groupMap = new HashMap<>();
		if (aggregate.equals("count")) {
			for (int i = 0; i < dataList.size(); i++) {
				JSONObject data = dataList.getJSONObject(i);
				String group = data.getString(groupField);
				if(StringUtils.isBlank(group)){
					throw new DashboardFieldNotFoundException(group);
				}
				if(group.startsWith("{")&&group.endsWith("}")) {
					JSONObject groupJson = ((JSONObject)JSONObject.parse(group));
					group = groupJson.getString("value");
					groupMap.put(group, groupJson.getString("text"));
				}
				if(StringUtils.isNotBlank(subGroupField)) {
					subGroup = data.getString(subGroupField);
					if(subGroup.startsWith("{")&&subGroup.endsWith("}")) {
						JSONObject subGroupJson = ((JSONObject)JSONObject.parse(subGroup));
						subGroup = subGroupJson.getString("value");
						groupMap.put(subGroup, subGroupJson.getString("text"));
					}
					if(StringUtils.isBlank(subGroup)){
						throw new DashboardFieldNotFoundException(subGroup);
					}
					String groupCombine = group+"#"+subGroup;
					if (!resultMap.containsKey(group)) {
						resultMap.put(groupCombine, 1D);
					} else {
						resultMap.put(groupCombine, resultMap.get(groupCombine) + 1D);
					}
				}else {
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
				String[] keys = key.split("#"); 
				if(keys.length >1) {
					data.put("column_x", groupMap.get(keys[0]));
					data.put("column_y", groupMap.get(keys[1]));
				}else {
					data.put("column", key);
				}
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

package codedriver.framework.dashboard.core.charts;

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
	public JSONArray getData(JSONObject dataMap) {
		Map<String,Object> resultMap = (Map<String,Object>)dataMap;
		if (MapUtils.isNotEmpty(resultMap)) {
			Iterator<String> itKey = resultMap.keySet().iterator();
			JSONArray returnList = new JSONArray();
			while (itKey.hasNext()) {
				String key = itKey.next();
				JSONObject data = new JSONObject();
				String[] keys = key.split("#"); 
				if(keys.length >1) {
					data.put("column_x", keys[0]);
					data.put("column_y", keys[1]);
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

	@Override
	public JSONObject getDataMap(JSONArray nextDataList, JSONObject configObj, JSONObject preDatas) {
		String groupField = configObj.getString("groupField");
		String subGroupField = configObj.getString("subGroupField");
		String aggregate = configObj.getString("aggregate");
		String subGroup = StringUtils.EMPTY;
		Map<String, Object> resultMap = (Map<String,Object>)preDatas;
		if (aggregate.equals("count")) {
			for (int i = 0; i < nextDataList.size(); i++) {
				JSONObject data = nextDataList.getJSONObject(i);
				String group = data.getString(groupField);
				if(StringUtils.isBlank(group)){
					throw new DashboardFieldNotFoundException(group);
				}
				if(StringUtils.isNotBlank(subGroupField)) {
					subGroup = data.getString(subGroupField);
					if(StringUtils.isBlank(subGroup)){
						throw new DashboardFieldNotFoundException(subGroup);
					}
					String groupCombine = group+"#"+subGroup;
					if (!resultMap.containsKey(groupCombine)) {
						resultMap.put(groupCombine, 1);
					} else {
						resultMap.put(groupCombine, Integer.valueOf(resultMap.get(groupCombine).toString()) + 1);
					}
				}else {
					if (!resultMap.containsKey(group)) {
						resultMap.put(group, 1);
					} else {
						resultMap.put(group, Integer.valueOf(resultMap.get(group).toString()) + 1);
					}
				}
			}
		} 
		return new JSONObject(resultMap);
	}

}

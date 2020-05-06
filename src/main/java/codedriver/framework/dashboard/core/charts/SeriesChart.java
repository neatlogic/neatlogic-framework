package codedriver.framework.dashboard.core.charts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.core.DashboardChartBase;

public class SeriesChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] {"areachart", "columnchart", "linechart", "stackbarchart", "stackcolumnchart" };
	}

	public static void main(String[] argv) {
		JSONObject obj = new JSONObject();
		obj.put("value", "abc");
		System.out.println(obj.getLongValue("value"));
	}

	@Override
	public JSONArray getData(JSONObject dataMap) {
		Map<String,Object> resultMap = (Map<String,Object>)dataMap;

		if (MapUtils.isNotEmpty(resultMap)) {
			Iterator<String> itKey = resultMap.keySet().iterator();
			JSONArray returnList = new JSONArray();
			while (itKey.hasNext()) {
				//String key = itKey.next();
				JSONObject data = new JSONObject();
				
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
		String valueField = configObj.getString("valueField");
		String groupField = configObj.getString("groupField");
		String subGroupField = configObj.getString("subGroupField");
		String aggregate = configObj.getString("aggregate");
		Map<String, Double> resultMap = new HashMap<>();
		if (aggregate.equals("count")) {
			for (int i = 0; i < nextDataList.size(); i++) {
				JSONObject data = nextDataList.getJSONObject(i);
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
			for (int i = 0; i < nextDataList.size(); i++) {
				JSONObject data = nextDataList.getJSONObject(i);
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
		return null;
	}



}

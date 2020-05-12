package codedriver.framework.dashboard.core.charts;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.core.DashboardChartBase;

public class StackTableChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] {"stacktablechart"};
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getData(JSONObject allData) {
		Map<String, Map<String, Integer>> dataMap = (Map<String, Map<String, Integer>>) allData.get("dataMap");
		JSONArray theadArray = allData.getJSONArray("theadList");
		JSONArray columnArray = allData.getJSONArray("columnList");
		JSONArray dataList = new JSONArray();
		for(Object columnObj : columnArray) {
			String column = ((JSONObject)columnObj).getString("name");
			JSONObject dataJson = new JSONObject();
			for(Object theadObj : theadArray ) {
				String thead = ((JSONObject)theadObj).getString("name");
				if(dataMap.containsKey(column)&&dataMap.get(column).containsKey(thead)) {
					dataJson.put(thead, dataMap.get(column).get(thead));
				}else {
					dataJson.put(thead, 0);
				}
			}
			dataList.add(dataJson);
		}
		allData.put("dataList", dataList);
		return allData;
	}

	@Override
	public JSONObject getChartConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getDataMap(JSONArray nextDataList, JSONObject configObj, JSONObject preDatas) {
		String groupField = configObj.getString("groupField");
		String subGroupField = configObj.getString("subGroupField");
		String aggregate = configObj.getString("aggregate");
		Map<String,Map<String,Integer>> dataMap = null;
		JSONArray theadArray = null;
		JSONArray columnArray = null;
		if(preDatas.containsKey("dataMap")) {
			dataMap = (Map<String, Map<String, Integer>>) preDatas.get("dataMap");
		}else {
			dataMap = new HashMap<String,Map<String,Integer>>();
			preDatas.put("dataMap",dataMap);
		}
		if(preDatas.containsKey("theadList")) {
			theadArray = preDatas.getJSONArray("theadList");
		}else {
			theadArray = new JSONArray();
			preDatas.put("theadList",theadArray);
		}
		if(preDatas.containsKey("columnList")) {
			columnArray = preDatas.getJSONArray("columnList");
		}else {
			columnArray = new JSONArray();
			preDatas.put("columnList",columnArray);
		}
		if (aggregate.equals("count")) {
			for (int i = 0; i < nextDataList.size(); i++) {
				JSONObject dataJson = nextDataList.getJSONObject(i);
				String subGroup = dataJson.getString(subGroupField);
				if(StringUtils.isNotBlank(subGroup)){
					String group = dataJson.getString(groupField);
					if(StringUtils.isNotBlank(group)){
						if(!theadArray.stream().anyMatch(o->((JSONObject)o).getString("name").equals(group))) {
							theadArray.add(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", group,group)));
						}
						if(dataMap.containsKey(subGroup)) {
							if(dataMap.get(subGroup).containsKey(group)) {
								Integer num = dataMap.get(subGroup).get(group);
								dataMap.get(subGroup).put(group, num+1);
							}else {
								dataMap.get(subGroup).put(group, 1);
							}
						}else {
							columnArray.add(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", subGroup,subGroup)));
							Map<String,Integer> subGoupMap = new HashMap<String,Integer>();
							subGoupMap.put(group, 1);
							dataMap.put(subGroup, subGoupMap);
						}
					    
					}
				}
			}
				
		} 
		return preDatas;
	}
}

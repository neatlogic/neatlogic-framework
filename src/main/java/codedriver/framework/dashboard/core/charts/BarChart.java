package codedriver.framework.dashboard.core.charts;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.dashboard.DashboardShowConfig;
import codedriver.framework.dashboard.core.DashboardChartBase;

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
		JSONObject charConfig = new JSONObject();
		JSONObject showConfig = new JSONObject();
		//AGGREGATE
		JSONObject aggregateJson = new JSONObject();
		aggregateJson.put("setting", DashboardShowConfig.AGGREGATE.getValue());
		aggregateJson.put("settingName", DashboardShowConfig.AGGREGATE.getText());
		JSONArray aggregateDataList = JSONArray.parseArray("[{'value':'count','text':'计数'}]");
		aggregateJson.put("dataList", aggregateDataList);
		showConfig.put(DashboardShowConfig.AGGREGATE.getValue(),aggregateJson);
		//GROUPFIELD
		JSONObject groupFieldJson = new JSONObject();
		groupFieldJson.put("setting", DashboardShowConfig.GROUPFIELD.getValue());
		groupFieldJson.put("settingName", DashboardShowConfig.GROUPFIELD.getText());
		groupFieldJson.put("dataList", new JSONArray());
		showConfig.put(DashboardShowConfig.GROUPFIELD.getValue(),groupFieldJson);
		//SUBGROUPFIELD
		JSONObject subgroupFieldJson = new JSONObject();
		subgroupFieldJson.put("setting", DashboardShowConfig.SUBGROUPFIELD.getValue());
		subgroupFieldJson.put("settingName", DashboardShowConfig.SUBGROUPFIELD.getText());
		subgroupFieldJson.put("dataList", new JSONArray());
		showConfig.put(DashboardShowConfig.SUBGROUPFIELD.getValue(),subgroupFieldJson);
		//MAXGROUP
		JSONObject maxgroupJson = new JSONObject();
		maxgroupJson.put("setting", DashboardShowConfig.MAXGROUP.getValue());
		maxgroupJson.put("settingName", DashboardShowConfig.MAXGROUP.getText());
		JSONArray maxgroupDataList = JSONArray.parseArray("[{'value':'10','text':'10'},{'value':'20','text':'20'}]");
		maxgroupJson.put("dataList", maxgroupDataList);
		showConfig.put(DashboardShowConfig.MAXGROUP.getValue(),maxgroupJson);
		//REFRESHTIME
		JSONObject refreshtimeJson = new JSONObject();
		refreshtimeJson.put("setting", DashboardShowConfig.REFRESHTIME.getValue());
		refreshtimeJson.put("settingName", DashboardShowConfig.REFRESHTIME.getText());
		JSONArray refreshtimeDataList = JSONArray.parseArray("[{'value':'-1','text':'不刷新'},{'value':'30','text':'30'}]");
		refreshtimeJson.put("dataList", refreshtimeDataList);
		showConfig.put(DashboardShowConfig.REFRESHTIME.getValue(),refreshtimeJson);
		
		charConfig.put("showConfig", showConfig);
		return charConfig;
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
					//throw new DashboardFieldNotFoundException(groupField);
				}else {
					if(StringUtils.isNotBlank(subGroupField)) {
						subGroup = data.getString(subGroupField);
						if(StringUtils.isBlank(subGroup)){
							//throw new DashboardFieldNotFoundException(subGroup);
						}else {
							String groupCombine = group+"#"+subGroup;
							if (!resultMap.containsKey(groupCombine)) {
								resultMap.put(groupCombine, 1);
							} else {
								resultMap.put(groupCombine, Integer.valueOf(resultMap.get(groupCombine).toString()) + 1);
							}
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
		} 
		return new JSONObject(resultMap);
	}
}

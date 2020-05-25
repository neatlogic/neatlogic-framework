package codedriver.framework.dashboard.core.charts;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.dashboard.ChartType;
import codedriver.framework.common.constvalue.dashboard.DashboardShowConfig;
import codedriver.framework.dashboard.core.DashboardChartBase;
import codedriver.framework.dashboard.dto.DashboardShowConfigVo;

public class TableChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] {ChartType.TABLECHART.getValue()};
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getData(JSONObject allData) {
		Map<String, Object> dataMap = (Map<String, Object>) allData.get("dataMap");
		JSONArray theadArray = allData.getJSONArray("theadList");
		JSONArray columnArray = allData.getJSONArray("columnList");
		JSONObject configObj = allData.getJSONObject("configObj");
		JSONArray dataList = new JSONArray();
		for(Object columnObj : columnArray) {
			String column = ((JSONObject)columnObj).getString("name");
			JSONObject dataJson = new JSONObject();
			if(StringUtils.isNotBlank(configObj.getString(DashboardShowConfig.SUBGROUPFIELD.getValue()))) {
				for(Object theadObj : theadArray ) {
					String thead = ((JSONObject)theadObj).getString("name");
					 Map<String, Integer> theadMap = (Map<String, Integer>)dataMap.get(column);
					if(dataMap.containsKey(column)&&theadMap.containsKey(thead)) {
						dataJson.put(thead, theadMap.get(thead));
					}else {
						dataJson.put(thead, 0);
					}
				}
			}else {
				dataJson.put(theadArray.getJSONObject(0).getString("displayName"), dataMap.get(column));
			}
			
			dataList.add(dataJson);
		}
		allData.put("dataList", dataList);
		return allData;
	}

	@Override
	public JSONObject getChartConfig() {
		JSONObject charConfig = new JSONObject();
		JSONObject showConfig = new JSONObject();
		showConfig.put(DashboardShowConfig.AGGREGATE.getValue(),new DashboardShowConfigVo(DashboardShowConfig.AGGREGATE,JSONArray.parseArray("[{'value':'count','text':'计数','isDefault':1}]")));
		showConfig.put(DashboardShowConfig.GROUPFIELD.getValue(),new DashboardShowConfigVo(DashboardShowConfig.GROUPFIELD,new JSONArray()));
		showConfig.put(DashboardShowConfig.SUBGROUPFIELD.getValue(),new DashboardShowConfigVo(DashboardShowConfig.SUBGROUPFIELD,new JSONArray()));
		showConfig.put(DashboardShowConfig.MAXGROUP.getValue(),new DashboardShowConfigVo(DashboardShowConfig.MAXGROUP,JSONArray.parseArray("[{'value':'10','text':'10','isDefault':1},{'value':'20','text':'20'}]")));
		showConfig.put(DashboardShowConfig.REFRESHTIME.getValue(),new DashboardShowConfigVo(DashboardShowConfig.REFRESHTIME,JSONArray.parseArray("[{'value':'-1','text':'不刷新','isDefault':1},{'value':'30','text':'30'}]")));
		charConfig.put("showConfig", showConfig);
		return charConfig;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getDataMap(JSONArray nextDataList, JSONObject configObj, JSONObject preDatas) {
		String groupField = configObj.getString(DashboardShowConfig.GROUPFIELD.getValue());
		String subGroupField = configObj.getString(DashboardShowConfig.SUBGROUPFIELD.getValue());
		String aggregate = configObj.getString(DashboardShowConfig.AGGREGATE.getValue());
		Map<String,Object> dataMap = null;
		JSONArray theadArray = null;
		JSONArray columnArray = null;
		if(!preDatas.containsKey("configObj")) {
			preDatas.put("configObj", configObj);
		}
		if(preDatas.containsKey("dataMap")) {
			dataMap = (Map<String, Object>) preDatas.get("dataMap");
		}else {
			dataMap = new HashMap<String,Object>();
			preDatas.put("dataMap",dataMap);
		}
		if(preDatas.containsKey("theadList")) {
			theadArray = preDatas.getJSONArray("theadList");
		}else {
			theadArray = new JSONArray();
			preDatas.put("theadList",theadArray);
			//theadArray.add(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", groupField,groupField)));
			if (StringUtils.isBlank(subGroupField)&&aggregate.equals("count")) {
				theadArray.add(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", "总数","总数")));
			}
		}
		if(preDatas.containsKey("columnList")) {
			columnArray = preDatas.getJSONArray("columnList");
		}else {
			columnArray = new JSONArray();
			preDatas.put("columnList",columnArray);
		}
		if (aggregate.equals("count")) {
			if(StringUtils.isNotBlank(subGroupField)){
				for (int i = 0; i < nextDataList.size(); i++) {
					JSONObject dataJson = nextDataList.getJSONObject(i);
					JSONObject group = dataJson.getJSONObject(groupField);
					JSONObject subGroup = dataJson.getJSONObject(subGroupField);
					String value = group.getString("value");
					String subValue = subGroup.getString("value");
					if(!theadArray.contains(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", subValue,subGroup.getString("text"))))) {
						theadArray.add(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", subValue,subGroup.getString("text"))));
					}
					if(dataMap.containsKey(value)) {
						 Map<String, Integer> subGroupMap = (Map<String, Integer>)dataMap.get(value);
						if(subGroupMap.containsKey(subValue)) {
							Integer num = subGroupMap.get(subValue);
							subGroupMap.put(subValue, num+1);
						}else {
							subGroupMap.put(subValue, 1);
						}
					}else {
						columnArray.add(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", value, group.getString("text"))));
						Map<String,Integer> subGoupMap = new HashMap<String,Integer>();
						subGoupMap.put(subValue, 1);
						dataMap.put(value, subGoupMap);
					}
				}
			}else {
				for (int i = 0; i < nextDataList.size(); i++) {
					JSONObject dataJson = nextDataList.getJSONObject(i);
					JSONObject group = dataJson.getJSONObject(groupField);
					String value = group.getString("value");
					if(dataMap.containsKey(value)) {
						dataMap.put(value, ((int)dataMap.get(value))+1);
					}else {
						columnArray.add(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", value,group.getString("text"))));
						dataMap.put(value, 1);
					}
				}	
			}
		}
		return preDatas;
	}
}

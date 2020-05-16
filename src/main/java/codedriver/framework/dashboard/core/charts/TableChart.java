package codedriver.framework.dashboard.core.charts;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.dashboard.DashboardShowConfig;
import codedriver.framework.common.constvalue.dashboard.ChartType;
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
			if(StringUtils.isNotBlank(configObj.getString("subGroupField"))) {
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
		showConfig.put(DashboardShowConfig.AGGREGATE.getValue(),new DashboardShowConfigVo(DashboardShowConfig.AGGREGATE,JSONArray.parseArray("[{'value':'count','text':'计数'}]")));
		showConfig.put(DashboardShowConfig.GROUPFIELD.getValue(),new DashboardShowConfigVo(DashboardShowConfig.GROUPFIELD,new JSONArray()));
		showConfig.put(DashboardShowConfig.SUBGROUPFIELD.getValue(),new DashboardShowConfigVo(DashboardShowConfig.SUBGROUPFIELD,new JSONArray()));
		showConfig.put(DashboardShowConfig.MAXGROUP.getValue(),new DashboardShowConfigVo(DashboardShowConfig.MAXGROUP,JSONArray.parseArray("[{'value':'10','text':'10'},{'value':'20','text':'20'}]")));
		showConfig.put(DashboardShowConfig.REFRESHTIME.getValue(),new DashboardShowConfigVo(DashboardShowConfig.REFRESHTIME,JSONArray.parseArray("[{'value':'-1','text':'不刷新'},{'value':'30','text':'30'}]")));
		charConfig.put("showConfig", showConfig);
		return charConfig;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getDataMap(JSONArray nextDataList, JSONObject configObj, JSONObject preDatas) {
		String groupField = configObj.getString("groupField");
		String subGroupField = configObj.getString("subGroupField");
		String aggregate = configObj.getString("aggregate");
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
					String group = dataJson.getString(groupField);
					String subGroup = dataJson.getString(subGroupField);
					if(!theadArray.contains(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", subGroup,subGroup)))) {
						theadArray.add(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", subGroup,subGroup)));
					}
					if(dataMap.containsKey(group)) {
						System.out.println(group);
						 Map<String, Integer> subGroupMap = (Map<String, Integer>)dataMap.get(group);
						if(subGroupMap.containsKey(subGroup)) {
							Integer num = subGroupMap.get(subGroup);
							subGroupMap.put(subGroup, num+1);
						}else {
							subGroupMap.put(subGroup, 1);
						}
					}else {
						columnArray.add(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", group,group)));
						Map<String,Integer> subGoupMap = new HashMap<String,Integer>();
						subGoupMap.put(subGroup, 1);
						dataMap.put(group, subGoupMap);
					}
				}
			}else {
				for (int i = 0; i < nextDataList.size(); i++) {
					JSONObject dataJson = nextDataList.getJSONObject(i);
					String group = dataJson.getString(groupField);
					if(dataMap.containsKey(group)) {
						dataMap.put(group, ((int)dataMap.get(group))+1);
					}else {
						columnArray.add(JSONObject.parse(String.format("{'name': '%s','displayName':'%s'}", group,group)));
						dataMap.put(group, 1);
					}
				}	
			}
		}
		return preDatas;
	}
}

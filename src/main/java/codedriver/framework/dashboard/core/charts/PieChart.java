package codedriver.framework.dashboard.core.charts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.dashboard.ChartType;
import codedriver.framework.common.constvalue.dashboard.DashboardShowConfig;
import codedriver.framework.dashboard.core.DashboardChartBase;
import codedriver.framework.dashboard.dto.DashboardShowConfigVo;

public class PieChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] { ChartType.PIECHART.getValue(),ChartType.DONUTCHART.getValue()  };
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getData(JSONObject dataMap) {
		JSONObject dataJson = new JSONObject();
		JSONArray dataList = new JSONArray();
		Map<String,Object> resultMap = (Map<String,Object>)dataMap.get("resultMap");
		Map<String, String> valueTextMap = (Map<String,String>)dataMap.get("valueTextMap");
		if (MapUtils.isNotEmpty(resultMap)) {
			Iterator<String> itKey = resultMap.keySet().iterator();
			while (itKey.hasNext()) {
				String key = itKey.next();
				if(!valueTextMap.containsKey(key)) {
					continue;
				}
				JSONObject data = new JSONObject();
				data.put("column", valueTextMap.get(key));
				data.put("value", resultMap.get(key));
				data.put("total", resultMap.get(key));
				dataList.add(data);
			}
			dataJson.put("dataList", dataList);
		}
		return dataJson;
	}

	@Override
	public JSONObject getChartConfig() {
		JSONObject charConfig = new JSONObject();
		JSONObject showConfig = new JSONObject();
		showConfig.put(DashboardShowConfig.TYPE.getValue(),new DashboardShowConfigVo(DashboardShowConfig.TYPE,JSONArray.parseArray("[{'value':'donut','text':'环形'},{'value':'pie','text':'实心','isDefault':1}]")));
		showConfig.put(DashboardShowConfig.AGGREGATE.getValue(),new DashboardShowConfigVo(DashboardShowConfig.AGGREGATE,JSONArray.parseArray("[{'value':'count','text':'计数','isDefault':1}]")));
		showConfig.put(DashboardShowConfig.GROUPFIELD.getValue(),new DashboardShowConfigVo(DashboardShowConfig.GROUPFIELD,new JSONArray()));
		showConfig.put(DashboardShowConfig.MAXGROUP.getValue(),new DashboardShowConfigVo(DashboardShowConfig.MAXGROUP,JSONArray.parseArray("[{'value':'10','text':'10','isDefault':1},{'value':'20','text':'20'}]")));
		showConfig.put(DashboardShowConfig.REFRESHTIME.getValue(),new DashboardShowConfigVo(DashboardShowConfig.REFRESHTIME,JSONArray.parseArray("[{'value':'-1','text':'不刷新','isDefault':1},{'value':'30','text':'30'}]")));
		charConfig.put("showConfig", showConfig);
		return charConfig;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getDataMap(JSONArray nextDataList, JSONObject configObj, JSONObject preDatas) {
		String groupField = configObj.getString(DashboardShowConfig.GROUPFIELD.getValue());
		String aggregate = configObj.getString(DashboardShowConfig.AGGREGATE.getValue());
		Map<String, Object> resultMap = null;
		Map<String, String> valueTextMap = null;
		if (aggregate.equals("count")) {
			for (int i = 0; i < nextDataList.size(); i++) {
				JSONObject data = nextDataList.getJSONObject(i);
				JSONArray  groupArray = new JSONArray();
				Object groupObj = data.get(groupField);
				if(groupObj instanceof JSONObject) {
					groupArray.add(groupObj);
				}else if(groupObj instanceof JSONArray){
					groupArray = (JSONArray) groupObj;
				}else {
					continue;
				}
				for(Object group :groupArray) {
					String value = StringUtils.EMPTY;
					if(preDatas.containsKey("resultMap")) {
						resultMap = (Map<String,Object>)preDatas.get("resultMap");
					}else {
						resultMap =  new HashMap<String,Object>();
						preDatas.put("resultMap", resultMap);
					}
					if(preDatas.containsKey("valueTextMap")) {
						valueTextMap = (Map<String,String>)preDatas.get("valueTextMap");
					}else {
						valueTextMap =  new HashMap<String,String>();
						preDatas.put("valueTextMap", valueTextMap);
					}
					if(group != null) {
						value = ((JSONObject)group).getString("value");
						valueTextMap.put(value, ((JSONObject)group).getString("text"));
					}
					if (!resultMap.containsKey(value)) {
						resultMap.put(value, 1);
					} else {
						resultMap.put(value, Integer.valueOf(resultMap.get(value).toString()) + 1);
					}
				}
			}
		} 
		return preDatas;
	}
}

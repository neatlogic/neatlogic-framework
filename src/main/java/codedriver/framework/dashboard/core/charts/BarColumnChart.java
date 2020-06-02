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

public class BarColumnChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] {ChartType.BARCHART.getValue(),ChartType.STACKBARCHART.getValue(),ChartType.COLUMNCHART.getValue(),ChartType.STACKCOLUMNCHART.getValue()};
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getData(JSONObject dataMap) {
		JSONObject dataJson = new JSONObject();
		JSONArray dataList = new JSONArray();
		Map<String,Object> resultMap = (Map<String,Object>)dataMap.get("resultMap");
		Map<String,Integer> columnTotalMap = new HashMap<String,Integer>();
		Map<String, String> valueTextMap = (Map<String,String>)dataMap.get("valueTextMap");
		if (MapUtils.isNotEmpty(resultMap)) {
			Iterator<String> itKey1 = resultMap.keySet().iterator();
			//统计column total，后续排序 limit
			while (itKey1.hasNext()) {
				String key = itKey1.next();
				String[] keys = key.split("#");
				int total = 0;
				if(columnTotalMap.containsKey(keys[0])) {
					total = columnTotalMap.get(keys[0]);
				}
				columnTotalMap.put(keys[0],total+(int)resultMap.get(key));

			}
			Iterator<String> itKey = resultMap.keySet().iterator();
			while (itKey.hasNext()) {
				String key = itKey.next();
				JSONObject data = new JSONObject();
				String[] keys = key.split("#"); 
				if(keys.length >1) {
					data.put("column", valueTextMap.get(keys[0]));
					data.put("type", valueTextMap.get(keys[1]));
				}else {
					data.put("column", valueTextMap.get(key));
				}
				data.put("total", columnTotalMap.get(keys[0]));
				data.put("value", resultMap.get(key));
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
		showConfig.put(DashboardShowConfig.TYPE.getValue(),new DashboardShowConfigVo(DashboardShowConfig.TYPE,JSONArray.parseArray("[{'value':'bar','text':'横向'},{'value':'column','text':'纵向','isDefault':1}]")));
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
		JSONObject subGroup = null;
		Map<String, Object> resultMap = null;
		Map<String, String> valueTextMap = null;
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
		if (aggregate.equals("count")) {
			for (int i = 0; i < nextDataList.size(); i++) {
				JSONObject data = nextDataList.getJSONObject(i);
				JSONObject group = data.getJSONObject(groupField);
				String value = StringUtils.EMPTY;
				if(group != null) {
					value = group.getString("value");
					valueTextMap.put(value, group.getString("text"));
				}
				if(StringUtils.isBlank(value)){
					//throw new DashboardFieldNotFoundException(groupField);
				}else {
					if(StringUtils.isNotBlank(subGroupField)) {
						subGroup = data.getJSONObject(subGroupField);
						String subValue = StringUtils.EMPTY;
						if(subGroup != null) {
							subValue = subGroup.getString("value");
							valueTextMap.put(subValue, subGroup.getString("text"));
						}

						String groupCombine = value+"#"+subValue;
						if (!resultMap.containsKey(groupCombine)) {
							resultMap.put(groupCombine, 1);
						} else {
							resultMap.put(groupCombine, Integer.valueOf(resultMap.get(groupCombine).toString()) + 1);
						}

					}else {
						if (!resultMap.containsKey(value)) {
							resultMap.put(value, 1);
						} else {
							resultMap.put(value, Integer.valueOf(resultMap.get(value).toString()) + 1);
						}
					}
				}
			}
		} 
		return preDatas;
	}
}

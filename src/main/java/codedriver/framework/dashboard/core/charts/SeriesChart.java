package codedriver.framework.dashboard.core.charts;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.dashboard.DashboardShowConfig;
import codedriver.framework.dashboard.core.DashboardChartBase;
import codedriver.framework.dashboard.dto.DashboardShowConfigVo;

public class SeriesChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] {"areachart", "linechart", "stackbarchart", "stackcolumnchart" };
	}

	@Override
	public JSONObject getData(JSONObject dataMap) {
		JSONObject dataJson = new JSONObject();
		JSONArray dataList = new JSONArray();
		Map<String,Object> resultMap = (Map<String,Object>)dataMap;
		if (MapUtils.isNotEmpty(resultMap)) {
			Iterator<String> itKey = resultMap.keySet().iterator();
			while (itKey.hasNext()) {
				String key = itKey.next();
				JSONObject data = new JSONObject();
				String[] keys = key.split("#"); 
				if(keys.length >1) {
					data.put("column", keys[0]);
					data.put("type", keys[1]);
				}else {
					data.put("column", key);
				}
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
		showConfig.put(DashboardShowConfig.AGGREGATE.getValue(),new DashboardShowConfigVo(DashboardShowConfig.AGGREGATE,JSONArray.parseArray("[{'value':'count','text':'计数'}]")));
		showConfig.put(DashboardShowConfig.GROUPFIELD.getValue(),new DashboardShowConfigVo(DashboardShowConfig.GROUPFIELD,new JSONArray()));
		showConfig.put(DashboardShowConfig.SUBGROUPFIELD.getValue(),new DashboardShowConfigVo(DashboardShowConfig.SUBGROUPFIELD,new JSONArray()));
		showConfig.put(DashboardShowConfig.MAXGROUP.getValue(),new DashboardShowConfigVo(DashboardShowConfig.MAXGROUP,JSONArray.parseArray("[{'value':'10','text':'10'},{'value':'20','text':'20'}]")));
		showConfig.put(DashboardShowConfig.REFRESHTIME.getValue(),new DashboardShowConfigVo(DashboardShowConfig.REFRESHTIME,JSONArray.parseArray("[{'value':'-1','text':'不刷新'},{'value':'30','text':'30'}]")));
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

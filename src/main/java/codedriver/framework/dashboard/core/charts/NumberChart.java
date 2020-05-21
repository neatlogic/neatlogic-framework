package codedriver.framework.dashboard.core.charts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.dashboard.ChartType;
import codedriver.framework.common.constvalue.dashboard.DashboardShowConfig;
import codedriver.framework.dashboard.core.DashboardChartBase;
import codedriver.framework.dashboard.dto.DashboardShowConfigVo;

public class NumberChart extends DashboardChartBase {

	@Override
	public String[] getSupportChart() {
		return new String[] {ChartType.NUMBERCHART.getValue()};
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getData(JSONObject dataMap) {
		JSONObject dataJson = new JSONObject();
		JSONArray dataList = new JSONArray();
		Map<String,Object> resultMap = (Map<String,Object>)dataMap.get("resultMap");
		if (MapUtils.isNotEmpty(resultMap)) {
			Iterator<String> itKey = resultMap.keySet().iterator();
			while (itKey.hasNext()) {
				String key = itKey.next();
				JSONObject data = new JSONObject();
				data.put("column", key);
				data.put("value", resultMap.get(key));
				dataList.add(data);
			}
			dataJson.put("dataList", dataList);
		}else {
			dataJson.put("dataList", CollectionUtils.EMPTY_COLLECTION);
			
		}
		dataJson.put("total", dataMap.get("total"));
		return dataJson;
	}

	@Override
	public JSONObject getChartConfig() {
		JSONObject charConfig = new JSONObject();
		JSONObject showConfig = new JSONObject();
		showConfig.put(DashboardShowConfig.TYPE.getValue(),new DashboardShowConfigVo(DashboardShowConfig.TYPE,JSONArray.parseArray("[{'value':'single','text':'单值','isDefault':1},{'value':'many','text':'多值'}]")));
		showConfig.put(DashboardShowConfig.AGGREGATE.getValue(),new DashboardShowConfigVo(DashboardShowConfig.AGGREGATE,JSONArray.parseArray("[{'value':'count','text':'计数','isDefault':1}]")));
		showConfig.put(DashboardShowConfig.GROUPFIELD.getValue(),new DashboardShowConfigVo(DashboardShowConfig.GROUPFIELD,new JSONArray()));
		showConfig.put(DashboardShowConfig.MAXGROUP.getValue(),new DashboardShowConfigVo(DashboardShowConfig.MAXGROUP,JSONArray.parseArray("[{'value':'10','text':'10','isDefault':1},{'value':'20','text':'20'}]")));
		showConfig.put(DashboardShowConfig.REFRESHTIME.getValue(),new DashboardShowConfigVo(DashboardShowConfig.REFRESHTIME,JSONArray.parseArray("[{'value':'-1','text':'不刷新','isDefault':1},{'value':'30','text':'30'}]")));
		showConfig.put(DashboardShowConfig.COLOR.getValue(),new DashboardShowConfigVo(DashboardShowConfig.COLOR,JSONArray.parseArray("['#D18CBD','#FFBA5A','#78D8DE','#A78375','#B9D582','#898DDD','#F3E67B','#527CA6','#50BFF2','#FF6666','#15BF81','#90A4AE']")));
		charConfig.put("showConfig", showConfig);
		return charConfig;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getDataMap(JSONArray nextDataList, JSONObject configObj, JSONObject preDatas) {
		String groupField = configObj.getString(DashboardShowConfig.GROUPFIELD.getValue());
		String aggregate = configObj.getString(DashboardShowConfig.AGGREGATE.getValue());
		JSONArray configList = configObj.getJSONArray("configlist");
		Map<String, Object> resultMap = null;
		Integer total = 0;
		if(preDatas.containsKey("resultMap")) {
			resultMap = (Map<String,Object>)preDatas.get("resultMap");
		}else {
			resultMap =  new HashMap<String,Object>();
			preDatas.put("resultMap", resultMap);
		}
		
		if(preDatas.containsKey("total")) {
			total = preDatas.getInteger("total");
		}
		if (aggregate.equals("count")) {
			for (int i = 0; i < nextDataList.size(); i++) {
				JSONObject data = nextDataList.getJSONObject(i);
				String group = data.getString(groupField);
				if(CollectionUtils.isNotEmpty(configList)) {
					if(StringUtils.isBlank(group)){
						//throw new DashboardFieldNotFoundException(groupField);
					}else if(configList.contains(group)){
						if (!resultMap.containsKey(group)) {
							resultMap.put(group, 1);
						} else {
							resultMap.put(group, Integer.valueOf(resultMap.get(group).toString()) + 1);
						}
						total++;
					}
				}
			}
		} 
		preDatas.put("total", total);
		
		return preDatas;
	}
}
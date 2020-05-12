package codedriver.framework.dashboard.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class DashboardChartBase {
	/**
	 * 
	 * @Author: chenqiwei
	 * @Time:Mar 20, 2020
	 * @Description: 要和dashboard_widget表chart_type字段枚举值一致
	 * @param @return
	 * @return String
	 */
	public abstract String[] getSupportChart();

	/**
	 * 
	 * @Description: 流式返回数据
	 * @param @return
	 * @return JSONObject
	 */
	public abstract JSONObject getData(JSONObject dataMap);
	
	/**
	 * 
	 * @Description: 流式累加统计
	 * @param nextDataList
	 * @param configObj
	 * @param preDatas
	 * @return
	 */
	public abstract JSONObject getDataMap(JSONArray nextDataList, JSONObject configObj, JSONObject preDatas);
	
	/**
	 * 
	 * @Description: 回显规则
	 * @param @return
	 * @return JSONObject
	 */
	public abstract JSONObject getChartConfig(); 
	
}

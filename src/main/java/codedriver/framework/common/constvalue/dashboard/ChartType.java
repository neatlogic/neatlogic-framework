package codedriver.framework.common.constvalue.dashboard;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public enum ChartType {
	BARCHART("barchart","柱状图",Chart.BARCHART,false),
	STACKBARCHART("stackbarchart","堆叠柱状图",Chart.BARCHART,false),
	COLUMNCHART("columnchart","条形图",Chart.BARCHART,true),
	STACKCOLUMNCHART("stackcolumnchart","堆叠条形图",Chart.BARCHART,false),
	PIECHART("piechart","饼图",Chart.PIECHART,true),
	DONUTCHART("donutchart","环形图",Chart.PIECHART,true),
	AREACHART("areachart","面积图",Chart.AREACHART,true),
	LINECHART("linechart","曲线图",Chart.LINECHART,true),
	TABLECHART("tablechart","表格",Chart.TABLECHART,true),
	NUMBERCHART("numberchart","值图",Chart.NUMBERCHART,true)
	;

	private String value;
	private String text;
	private Chart chart;
	private Boolean isDefault;

	private ChartType(String _value, String _text,Chart _chart,Boolean _isDefault) {
		this.value = _value;
		this.text = _text;
		this.chart = _chart;
		this.isDefault = _isDefault;
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

	public Chart getChart() {
		return chart;
	}
	
	public Boolean getIsDefault() {
		return isDefault;
	}

	public static String getValue(String _status) {
		for (ChartType s : ChartType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getValue();
			}
		}
		return null;
	}

	public static String getText(String _status) {
		for (ChartType s : ChartType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getText();
			}
		}
		return "";
	}
	
	public static Map<String,JSONArray> getSubChartMap(){
		Map<String,JSONArray> subCharMap = new HashMap<String,JSONArray>();
		for (ChartType s : ChartType.values()) {
			String chart = s.getChart().getValue();
			JSONArray subChartArray = subCharMap.get(chart);
			JSONObject subChartJson = new JSONObject();
			subChartJson.put("value", s.getValue());
			subChartJson.put("text", s.getText());
			subChartJson.put("isDefault", s.getIsDefault());
			if(subChartArray == null) {
				subChartArray = new JSONArray();
			}
			subChartArray.add(subChartJson);
			subCharMap.put(chart, subChartArray);
		}
		return subCharMap;
	}
	
	public static JSONArray getSubChartList(String value){
		JSONArray subChartArray = new JSONArray();
		for (ChartType s : ChartType.values()) {
			String chart = s.getChart().getValue();
			if(StringUtils.isNotBlank(value)&&chart.equals(value)) {
				JSONObject subChartJson = new JSONObject();
				subChartJson.put("value", s.getValue());
				subChartJson.put("text", s.getText());
				subChartJson.put("isDefault", s.getIsDefault());
				subChartArray.add(subChartJson);
			}
		}
		return subChartArray;
	}
}

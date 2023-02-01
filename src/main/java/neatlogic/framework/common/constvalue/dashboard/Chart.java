package neatlogic.framework.common.constvalue.dashboard;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public enum Chart {

	BARCHART("barchart","柱状图","tsfont-chart-bar"),
	PIECHART("piechart","饼图","tsfont-chart-pie"),
	LINECHART("linechart","曲线图","tsfont-chart-line"),
	TABLECHART("tablechart","表格","tsfont-chart-table"),
	NUMBERCHART("numberchart","值图","tsfont-chart-number"),
	AREACHART("areachart","面积图","tsfont-chart-area"),
	;

	private String value;
	private String text;
	private String icon;

	private Chart(String _value, String _text,String _icon) {
		this.value = _value;
		this.text = _text;
		this.icon = _icon;
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

	public String getIcon() {
		return icon;
	}

	public static String getValue(String _status) {
		for (Chart s : Chart.values()) {
			if (s.getValue().equals(_status)) {
				return s.getValue();
			}
		}
		return null;
	}

	public static String getText(String _status) {
		for (Chart s : Chart.values()) {
			if (s.getValue().equals(_status)) {
				return s.getText();
			}
		}
		return "";
	}
	
	public static JSONArray getChartList() {
		JSONArray chartArray = new JSONArray();
		for (Chart s : Chart.values()) {
			JSONObject chart = new JSONObject();
			chart.put("value", s.getValue());
			chart.put("text", s.getText());
			chart.put("icon", s.getIcon());
			chart.put("chartTypeList", ChartType.getSubChartList(s.getValue()));
			chartArray.add(chart);
		}
		return chartArray;
	}
	
	public static JSONObject getChart(String value) {
		JSONObject chart = new JSONObject();
		for (Chart s : Chart.values()) {
			if(s.getValue().equals(value)) {
				chart.put("value", s.getValue());
				chart.put("text", s.getText());
				chart.put("icon", s.getIcon());
				break;
			}
		}
		return chart;
	}
}

package neatlogic.framework.common.constvalue.dashboard;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum Chart {

	BARCHART("barchart",new I18n("common.barchart"),"tsfont-chart-bar"),
	PIECHART("piechart",new I18n("common.piechart"),"tsfont-chart-pie"),
	LINECHART("linechart",new I18n("common.curvechart"),"tsfont-chart-line"),
	TABLECHART("tablechart",new I18n("common.table"),"tsfont-chart-table"),
	NUMBERCHART("numberchart",new I18n("common.valuemap"),"tsfont-chart-number"),
	AREACHART("areachart",new I18n("common.areachart"),"tsfont-chart-area"),
	;

	private final String value;
	private final I18n text;
	private final String icon;

	private Chart(String _value, I18n _text,String _icon) {
		this.value = _value;
		this.text = _text;
		this.icon = _icon;
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return I18nUtils.getMessage(text.toString());
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

package neatlogic.framework.common.constvalue.dashboard;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum ChartType {
	BARCHART("barchart",new I18n("柱状图"),Chart.BARCHART,false),
	STACKBARCHART("stackbarchart",new I18n("堆叠柱状图"),Chart.BARCHART,false),
	COLUMNCHART("columnchart",new I18n("条形图"),Chart.BARCHART,true),
	STACKCOLUMNCHART("stackcolumnchart",new I18n("堆叠条形图"),Chart.BARCHART,false),
	PIECHART("piechart",new I18n("饼图"),Chart.PIECHART,true),
	DONUTCHART("donutchart",new I18n("环形图"),Chart.PIECHART,true),
	AREACHART("areachart",new I18n("面积图"),Chart.AREACHART,true),
	LINECHART("linechart",new I18n("曲线图"),Chart.LINECHART,true),
	TABLECHART("tablechart",new I18n("表格"),Chart.TABLECHART,true),
	NUMBERCHART("numberchart",new I18n("值图"),Chart.NUMBERCHART,true)
	;

	private String value;
	private I18n text;
	private Chart chart;
	private Boolean isDefault;

	private ChartType(String _value, I18n _text,Chart _chart,Boolean _isDefault) {
		this.value = _value;
		this.text = _text;
		this.chart = _chart;
		this.isDefault = _isDefault;
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return $.t(text.toString());
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

package codedriver.framework.dashboard.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class ChartDataVo {
	@EntityField(name = "值字段列表", type = ApiParamType.JSONARRAY)
	private List<String> valueFieldList;
	@EntityField(name = "图例字段名称", type = ApiParamType.STRING)
	private String legendField;
	@EntityField(name = "数据集", type = ApiParamType.JSONARRAY)
	private JSONArray dataList;
	@EntityField(name = "图表配置", type = ApiParamType.JSONOBJECT)
	private JSONObject configObj;

	public JSONArray getDataList() {
		return dataList;
	}

	public void setDataList(JSONArray dataList) {
		this.dataList = dataList;
	}

	public void addData(JSONObject data) {
		if (dataList == null) {
			dataList = new JSONArray();
		}
		dataList.add(data);
	}

	public void addValueField(String valueField) {
		if (StringUtils.isNotBlank(valueField)) {
			if (valueFieldList == null) {
				valueFieldList = new ArrayList<>();
			}
			valueFieldList.add(valueField);
		}
	}

	public String getLegendField() {
		return legendField;
	}

	public void setLegendField(String legendField) {
		this.legendField = legendField;
	}

	public JSONObject getConfigObj() {
		return configObj;
	}

	public void setConfigObj(JSONObject configObj) {
		this.configObj = configObj;
	}

	public List<String> getValueFieldList() {
		return valueFieldList;
	}

	public void setValueFieldList(List<String> valueFieldList) {
		this.valueFieldList = valueFieldList;
	}
}

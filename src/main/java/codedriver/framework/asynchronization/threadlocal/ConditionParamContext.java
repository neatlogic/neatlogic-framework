package codedriver.framework.asynchronization.threadlocal;

import org.apache.commons.collections4.MapUtils;

import com.alibaba.fastjson.JSONObject;

public class ConditionParamContext {

	private static ThreadLocal<ConditionParamContext> instance = new ThreadLocal<ConditionParamContext>();
	
	private JSONObject paramData = new JSONObject();
	private JSONObject paramNameData = new JSONObject();
	private JSONObject paramTextData = new JSONObject();
	
	public static ConditionParamContext init(JSONObject _paramData) {
		ConditionParamContext context = new ConditionParamContext();
		if(MapUtils.isNotEmpty(_paramData)) {
			context.paramData.putAll(_paramData);
		}
		instance.set(context);
		return context;
	}
	public static ConditionParamContext init(JSONObject _paramData, JSONObject _paramNameData, JSONObject _paramTextData) {
		ConditionParamContext context = new ConditionParamContext();
		if(MapUtils.isNotEmpty(_paramData)) {
			context.paramData.putAll(_paramData);
		}
		if(MapUtils.isNotEmpty(_paramNameData)) {
			context.paramNameData.putAll(_paramNameData);
		}
		if(MapUtils.isNotEmpty(_paramTextData)) {
			context.paramTextData.putAll(_paramTextData);
		}
		instance.set(context);
		return context;
	}
	public static ConditionParamContext get() {
		return instance.get();
	}
	public void release() {
		instance.remove();
	}

	private ConditionParamContext() {
		
	}
	
	public JSONObject getParamData() {
		return paramData;
	}

	public void setParamData(JSONObject paramData) {
		this.paramData = paramData;
	}
	public JSONObject getParamNameData() {
		return paramNameData;
	}
	public void setParamNameData(JSONObject paramNameData) {
		this.paramNameData = paramNameData;
	}
	public JSONObject getParamTextData() {
		return paramTextData;
	}
	public void setParamTextData(JSONObject paramTextData) {
		this.paramTextData = paramTextData;
	}
}

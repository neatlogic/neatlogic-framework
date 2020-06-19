package codedriver.framework.asynchronization.threadlocal;

import org.apache.commons.collections4.MapUtils;

import com.alibaba.fastjson.JSONObject;

public class ConditionParamContext {

	private static ThreadLocal<ConditionParamContext> instance = new ThreadLocal<ConditionParamContext>();
	
	private JSONObject paramData = new JSONObject();
	
	public static ConditionParamContext init(JSONObject _paramData) {
		ConditionParamContext context = new ConditionParamContext();
		if(MapUtils.isNotEmpty(_paramData)) {
			context.paramData.putAll(_paramData);
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
}

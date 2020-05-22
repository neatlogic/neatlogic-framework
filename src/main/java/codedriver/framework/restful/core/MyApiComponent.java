package codedriver.framework.restful.core;

import com.alibaba.fastjson.JSONObject;

public interface MyApiComponent extends IApiComponent {
	public abstract Object myDoService(JSONObject jsonObj) throws Exception;

	public default Object myDoTest(JSONObject jsonObj) {
		return null;
	}
}

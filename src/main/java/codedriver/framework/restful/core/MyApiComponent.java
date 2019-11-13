package codedriver.framework.restful.core;

import com.alibaba.fastjson.JSONObject;

public interface MyApiComponent extends ApiComponent {
	public abstract Object myDoService(JSONObject jsonObj) throws Exception;
}

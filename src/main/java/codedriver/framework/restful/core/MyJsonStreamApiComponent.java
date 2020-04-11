package codedriver.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;

public interface MyJsonStreamApiComponent extends IJsonStreamApiComponent{
	public abstract Object myDoService(JSONObject paramObj, JSONReader jsonReader) throws Exception;
}

package codedriver.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import codedriver.framework.restful.dto.ApiVo;

public interface JsonStreamApiComponent {
	public String getToken();

	public String getId();

	public String getName();

	public String getConfig();

	public Object doService(ApiVo interfaceVo, JSONObject paramObj, JSONReader jsonReader) throws Exception;

	public JSONObject help();
}

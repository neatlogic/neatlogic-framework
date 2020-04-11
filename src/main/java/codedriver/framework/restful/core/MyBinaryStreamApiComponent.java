package codedriver.framework.restful.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

public interface MyBinaryStreamApiComponent extends IBinaryStreamApiComponent {
	public abstract Object myDoService(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;
}

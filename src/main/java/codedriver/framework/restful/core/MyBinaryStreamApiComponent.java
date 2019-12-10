package codedriver.framework.restful.core;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;

public interface MyBinaryStreamApiComponent extends BinaryStreamApiComponent {
	public abstract Object myDoService(JSONObject paramObj, MultipartHttpServletRequest multipartRequest) throws Exception;
}

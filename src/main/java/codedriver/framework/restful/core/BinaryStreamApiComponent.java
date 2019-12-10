package codedriver.framework.restful.core;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.restful.dto.ApiVo;

public interface BinaryStreamApiComponent {
	public String getToken();

	public String getId();

	public String getName();

	public String getConfig();

	public Object doService(ApiVo interfaceVo, JSONObject paramObj, MultipartHttpServletRequest multipartRequest) throws Exception;

	public JSONObject help();
}

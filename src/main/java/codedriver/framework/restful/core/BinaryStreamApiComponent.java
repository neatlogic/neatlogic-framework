package codedriver.framework.restful.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.restful.dto.ApiVo;

public interface BinaryStreamApiComponent {
	public String getToken();

	public String getId();

	public String getName();

	public String getConfig();

	public boolean isPrivate();

	public Object doService(ApiVo interfaceVo, JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;

	public JSONObject help();
}

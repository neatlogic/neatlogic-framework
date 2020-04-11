package codedriver.framework.restful.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.restful.dto.ApiVo;

public interface IBinaryStreamApiComponent {
	public String getToken();

	public String getId();

	public String getName();

	// true时返回格式不再包裹固定格式
	public default boolean isRaw() {
		return false;
	}

	public String getConfig();

	public boolean isPrivate();

	public int needAudit();

	public Object doService(ApiVo interfaceVo, JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;

	public JSONObject help();
}

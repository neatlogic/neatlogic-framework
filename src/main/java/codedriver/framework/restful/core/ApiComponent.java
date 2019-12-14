package codedriver.framework.restful.core;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.restful.dto.ApiVo;

public interface ApiComponent {
	public String getToken();

	public String getId();

	public String getName();

	public String getConfig();

	public boolean isPrivate();

	public int needAudit();

	public Object doService(ApiVo apiVo, JSONObject jsonObj) throws Exception;

	public JSONObject help();
}

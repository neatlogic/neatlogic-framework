package codedriver.framework.integration.authtication.core;

import java.net.HttpURLConnection;

import com.alibaba.fastjson.JSONObject;

public interface IAuthenticateHandler {
	public String getType();

	public void authenticate(HttpURLConnection connection, JSONObject config);
}

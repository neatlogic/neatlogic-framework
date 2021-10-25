/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.authtication.handler;

import java.net.HttpURLConnection;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.integration.authentication.enums.AuthenticateType;
import codedriver.framework.integration.authtication.core.IAuthenticateHandler;

public class BasicAuthenticateHandler implements IAuthenticateHandler {

	@Override
	public String getType() {
		return AuthenticateType.BASIC.getValue();
	}

	@Override
	public void authenticate(HttpURLConnection connection, JSONObject config) {
		String username = config.getString("username");
		String password = config.getString("password");
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
			Base64.Encoder encoder = Base64.getEncoder();
			String key = username + ":" + password;
			connection.addRequestProperty("Authorization", "Basic " + encoder.encodeToString(key.getBytes()));
		}
	}

}

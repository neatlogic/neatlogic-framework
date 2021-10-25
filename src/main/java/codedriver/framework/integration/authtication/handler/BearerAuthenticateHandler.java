/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.authtication.handler;

import java.net.HttpURLConnection;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.integration.authentication.enums.AuthenticateType;
import codedriver.framework.integration.authtication.core.IAuthenticateHandler;

public class BearerAuthenticateHandler implements IAuthenticateHandler {
	@Override
	public String getType() {
		return AuthenticateType.BEARER.getValue();
	}

	@Override
	public void authenticate(HttpURLConnection connection, JSONObject config) {
		String token = config.getString("token");
		if (StringUtils.isNotBlank(token)) {
			connection.addRequestProperty("Authorization", "Bearer " + token);
		}
	}
}

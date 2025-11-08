/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.integration.authentication.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.integration.authentication.core.IAuthenticateHandler;
import neatlogic.framework.integration.authentication.enums.AuthenticateType;
import org.apache.commons.lang3.StringUtils;

import java.net.HttpURLConnection;
import java.util.Base64;

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

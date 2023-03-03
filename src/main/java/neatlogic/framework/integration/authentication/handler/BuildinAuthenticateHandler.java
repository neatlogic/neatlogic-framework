/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.integration.authentication.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.exception.integration.AuthenticateException;
import neatlogic.framework.integration.authentication.core.IAuthenticateHandler;
import neatlogic.framework.integration.authentication.enums.AuthenticateType;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

public class BuildinAuthenticateHandler implements IAuthenticateHandler {

    private final static Logger logger = LoggerFactory.getLogger(BuildinAuthenticateHandler.class);

    @Override
    public String getType() {
        return AuthenticateType.BUILDIN.getValue();
    }

    @Override
	public void authenticate(HttpURLConnection connection, JSONObject config) {
		UserContext context = UserContext.get();
		String token = context.getToken();
		if (StringUtils.isNotBlank(token)) {
			String authorization = null;
			if (token.startsWith("GZIP_")) {
				byte[] tokenDecoder = Base64.getDecoder().decode(token.substring(5));
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ByteArrayInputStream in = new ByteArrayInputStream(tokenDecoder);
				try {
					GZIPInputStream ungzip = new GZIPInputStream(in);
					byte[] buffer = new byte[1024];
					int n;
					while ((n = ungzip.read(buffer)) >= 0){
						out.write(buffer, 0, n);
					}
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
				authorization = out.toString();
			}else if (token.startsWith("Bearer_")) {
				authorization = token;
			}
			connection.addRequestProperty("Authorization", authorization);
			connection.addRequestProperty("Tenant", TenantContext.get().getTenantUuid());
		} else {
			throw new AuthenticateException("无法获取用户登录信息");
		}
	}
}

/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.integration.authentication.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.exception.integration.AuthenticateException;
import neatlogic.framework.integration.authentication.core.IAuthenticateHandler;
import neatlogic.framework.integration.authentication.enums.AuthenticateType;
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

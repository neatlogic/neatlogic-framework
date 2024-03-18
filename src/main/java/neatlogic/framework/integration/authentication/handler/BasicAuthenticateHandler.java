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

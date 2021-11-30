/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.authentication.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.exception.integration.AuthenticateException;
import codedriver.framework.integration.authentication.enums.AuthenticateType;
import codedriver.framework.integration.authentication.core.IAuthenticateHandler;
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
				authorization = new String(out.toByteArray());
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

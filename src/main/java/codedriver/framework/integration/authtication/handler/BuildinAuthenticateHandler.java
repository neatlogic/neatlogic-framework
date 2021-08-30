package codedriver.framework.integration.authtication.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.exception.integration.AuthenticateException;
import codedriver.framework.integration.authentication.costvalue.AuthenticateType;
import codedriver.framework.integration.authtication.core.IAuthenticateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

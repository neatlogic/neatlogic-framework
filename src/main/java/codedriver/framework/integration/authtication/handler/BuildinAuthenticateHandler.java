package codedriver.framework.integration.authtication.handler;

import java.net.HttpURLConnection;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.exception.integration.AuthenticateException;
import codedriver.framework.integration.authentication.costvalue.AuthenticateType;
import codedriver.framework.integration.authtication.core.IAuthenticateHandler;

public class BuildinAuthenticateHandler implements IAuthenticateHandler {

	@Override
	public String getType() {
		return AuthenticateType.BUILDIN.getValue();
	}

	@Override
	public void authenticate(HttpURLConnection connection, JSONObject config) {
		UserContext context = UserContext.get();
		if (StringUtils.isNotBlank(context.getToken())) {
			connection.addRequestProperty("Authorization", context.getToken());
			connection.addRequestProperty("Tenant", TenantContext.get().getTenantUuid());
		} else {
			throw new AuthenticateException("无法获取用户登录信息");
		}
	}

}

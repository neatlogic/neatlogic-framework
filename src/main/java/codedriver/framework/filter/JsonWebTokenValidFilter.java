package codedriver.framework.filter;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.ConfigMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.ConfigVo;
import codedriver.framework.dto.UserSessionVo;

public class JsonWebTokenValidFilter extends OncePerRequestFilter {
	// private ServletContext context;

	@Autowired
	UserMapper userMapper;

	@Autowired
	ConfigMapper configMapper;

	/**
	 * Default constructor.
	 */
	public JsonWebTokenValidFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		TenantContext.get().release();// 清除线程变量值
		UserContext.get().release();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		Cookie[] cookies = request.getCookies();
		boolean isAuth = false;
		String tenant = null, authorization = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("codedriver_tenant")) {
					tenant = cookie.getValue();
				} else if (cookie.getName().equals("codedriver_authorization")) {
					authorization = cookie.getValue();
				}
			}
		}
		if (StringUtils.isBlank(tenant)) {
			tenant = request.getHeader("Tenant");
			if (StringUtils.isNotBlank(tenant)) {
				Cookie tenantCookie = new Cookie("codedriver_tenant", tenant);
				tenantCookie.setPath("/");
				response.addCookie(tenantCookie);
			}
		}

		if (StringUtils.isBlank(authorization)) {
			authorization = request.getHeader("Authorization");
		}
		if (StringUtils.isNotBlank(authorization) && StringUtils.isNotBlank(tenant)) {
			if (authorization.startsWith("Bearer") && authorization.length() > 7) {
				String jwt = authorization.substring(7);
				String[] jwtParts = jwt.split("\\.");
				if (jwtParts.length == 3) {
					SecretKeySpec signingKey = new SecretKeySpec(Config.JWT_SECRET.getBytes(), "HmacSHA1");
					Mac mac;
					try {
						mac = Mac.getInstance("HmacSHA1");
						mac.init(signingKey);
						byte[] rawHmac = mac.doFinal((jwtParts[0] + "." + jwtParts[1]).getBytes());
						String result = Base64.getUrlEncoder().encodeToString(rawHmac);
						if (result.equals(jwtParts[2])) {
							isAuth = true;
						}
					} catch (NoSuchAlgorithmException | InvalidKeyException e) {
						e.printStackTrace();
					}
					if (isAuth) {
						String jwtBody = new String(Base64.getUrlDecoder().decode(jwtParts[1]), "utf-8");
						JSONObject jwtBodyObj = JSONObject.parseObject(jwtBody);
						TenantContext.init(tenant);
						UserContext.init(jwtBodyObj, request, response);
					}

				}
			}
		}
		boolean isValid = userExpirationValid();
		if (isAuth && isValid) {
			filterChain.doFilter(request, response);
		} else {
			JSONObject redirectObj = new JSONObject();
			if (!isAuth) {
				redirectObj.put("Status", "FAILED");
				redirectObj.put("Message", "没有找到登录信息，请登录");
			} else if (!isValid) {
				redirectObj.put("Status", "FAILED");
				redirectObj.put("Message", "会话已超时或已被终止，请重新登录");
			}
			// 清除cookies
			Cookie authCookie = new Cookie("codedriver_authorization", null);
			authCookie.setMaxAge(0);
			authCookie.setPath("/");
			Cookie tenantCookie = new Cookie("codedriver_tenant", null);
			tenantCookie.setMaxAge(0);
			tenantCookie.setPath("/");
			response.addCookie(authCookie);
			response.addCookie(tenantCookie);
			response.setContentType(Config.RESPONSE_TYPE_JSON);
			response.getWriter().print(redirectObj.toJSONString());
		}
	}

	private boolean userExpirationValid() {
		String userId = UserContext.get().getUserId();
		UserSessionVo userSessionVo = userMapper.getUserSessionByUserId(userId);
		if (null != userSessionVo) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date visitTime = formatter.parse(userSessionVo.getSessionTime());
				Date now = new Date();
				TenantContext.get().setUseDefaultDatasource(true);
				ConfigVo configVo = configMapper.getConfigByKey(UserSessionVo.USER_EXPIRETIME);
				TenantContext.get().setUseDefaultDatasource(false);
				Long expireTime = Long.parseLong(configVo != null ? configVo.getValue() : "60") * 60 * 1000 + visitTime.getTime();
				if (now.getTime() < expireTime) {
					userMapper.updateUserSession(userId);
					return true;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

}

package codedriver.framework.login;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.ReturnJson;
import codedriver.framework.common.config.Config;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.service.TenantService;
import codedriver.framework.service.UserService;

@Controller
@RequestMapping("/login/")
public class LoginController {
	Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private TenantService tenantService;

	@RequestMapping(value = "/check/{tenant}")
	public void dispatcherForPost(@RequestBody String json, @PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject returnObj = new JSONObject();
		JSONObject jsonObj = JSONObject.parseObject(json);
		TenantContext tenantContext = TenantContext.init();
		try {
			String userId = jsonObj.getString("userid");
			String password = jsonObj.getString("password");
			if (StringUtils.isBlank(tenant)) {
				tenant = request.getHeader("Tenant");
			}
			if (StringUtils.isNotBlank(tenant)) {
				// 使用master库
				tenantContext.setUseDefaultDatasource(true);
				TenantVo tenantVo = tenantService.getTenantByUuid(tenant);
				if (tenantVo == null) {
					throw new RuntimeException("找不到租户：" + tenant);
				}
				tenantContext.setTenantUuid(tenant);
				// 还原回租户库
				tenantContext.setUseDefaultDatasource(false);
			}

			UserVo userVo = new UserVo();
			userVo.setUserId(userId);
			userVo.setPassword(password);
			userVo.setTenant(tenant);

			UserVo checkUserVo = userService.getUserByUserIdAndPassword(userVo);
			if (checkUserVo != null) {
				//保存 user 登录访问时间
				userService.saveUserSession(checkUserVo.getUserId());
				
				JSONObject jwtHeadObj = new JSONObject();
				jwtHeadObj.put("alg", "HS256");
				jwtHeadObj.put("typ", "JWT");

				JSONObject jwtBodyObj = new JSONObject();
				jwtBodyObj.put("userid", checkUserVo.getUserId());
				jwtBodyObj.put("username", checkUserVo.getUserName());
				jwtBodyObj.put("tenant", tenant);
				if (checkUserVo.getRoleNameList() != null && checkUserVo.getRoleNameList().size() > 0) {
					JSONArray roleList = new JSONArray();
					for (String role : checkUserVo.getRoleNameList()) {
						roleList.add(role);
					}
					jwtBodyObj.put("rolelist", roleList);
				}

				String jwthead = Base64.getUrlEncoder().encodeToString(jwtHeadObj.toJSONString().getBytes());
				String jwtbody = Base64.getUrlEncoder().encodeToString(jwtBodyObj.toJSONString().getBytes());

				SecretKeySpec signingKey = new SecretKeySpec(Config.JWT_SECRET.getBytes(), "HmacSHA1");
				Mac mac;

				mac = Mac.getInstance("HmacSHA1");
				mac.init(signingKey);
				byte[] rawHmac = mac.doFinal((jwthead + "." + jwtbody).getBytes());
				String jwtsign = Base64.getUrlEncoder().encodeToString(rawHmac);

				Cookie authCookie = new Cookie("codedriver_authorization", "Bearer_" + jwthead + "." + jwtbody + "." + jwtsign);
				authCookie.setPath("/");
				Cookie tenantCookie = new Cookie("codedriver_tenant", tenant);
				tenantCookie.setPath("/");
				response.addCookie(authCookie);
				response.addCookie(tenantCookie);
				response.setContentType(Config.RESPONSE_TYPE_JSON);
				returnObj.put("Status", "OK");
				returnObj.put("JwtToken", jwthead + "." + jwtbody + "." + jwtsign);
				response.getWriter().print(returnObj);
			} else {
				throw new RuntimeException("用户验证失败");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			ReturnJson.error(ex.getMessage(), response);
		} finally {
			tenantContext.release();
		}
	}
}

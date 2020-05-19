package codedriver.framework.login;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
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
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.exception.tenant.TenantNotFoundException;
import codedriver.framework.exception.tenant.TenantUnActiveException;
import codedriver.framework.exception.user.UserAuthFailedException;
import codedriver.framework.service.TenantService;

@Controller
@RequestMapping("/login/")
public class LoginController {
	Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserMapper userMapper;

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
					throw new TenantNotFoundException(tenant);
				}
				if (tenantVo.getIsActive().equals(0)) {
					throw new TenantUnActiveException(tenant);
				}
				tenantContext.switchTenant(tenant);
				// 还原回租户库
				tenantContext.setUseDefaultDatasource(false);
			}

			UserVo userVo = new UserVo();
			userVo.setUserId(userId);
			userVo.setPassword(password);
			userVo.setTenant(tenant);

			UserVo checkUserVo = userMapper.getUserByUserIdAndPassword(userVo);
			if (checkUserVo != null) {
				// 保存 user 登录访问时间
				if(userMapper.getUserSessionByUserUuid(checkUserVo.getUuid()) != null) {
					userMapper.updateUserSession(checkUserVo.getUuid());
				}else {
					userMapper.insertUserSession(checkUserVo.getUuid());
				}
				JSONObject jwtHeadObj = new JSONObject();
				jwtHeadObj.put("alg", "HS256");
				jwtHeadObj.put("typ", "JWT");

				JSONObject jwtBodyObj = new JSONObject();
				jwtBodyObj.put("useruuid", checkUserVo.getUuid());
				jwtBodyObj.put("userid", checkUserVo.getUserId());
				jwtBodyObj.put("username", checkUserVo.getUserName());
				jwtBodyObj.put("tenant", tenant);
				if (CollectionUtils.isNotEmpty(checkUserVo.getRoleUuidList())) {
					JSONArray roleList = new JSONArray();
					for (String role : checkUserVo.getRoleUuidList()) {
						roleList.add(role);
					}
					jwtBodyObj.put("rolelist", roleList);
				}

				String jwthead = Base64.getUrlEncoder().encodeToString(jwtHeadObj.toJSONString().getBytes());
				String jwtbody = Base64.getUrlEncoder().encodeToString(jwtBodyObj.toJSONString().getBytes());

				SecretKeySpec signingKey = new SecretKeySpec(Config.JWT_SECRET().getBytes(), "HmacSHA1");
				Mac mac;

				mac = Mac.getInstance("HmacSHA1");
				mac.init(signingKey);
				byte[] rawHmac = mac.doFinal((jwthead + "." + jwtbody).getBytes());
				String jwtsign = Base64.getUrlEncoder().encodeToString(rawHmac);

				Cookie authCookie = new Cookie("codedriver_authorization", "Bearer_" + jwthead + "." + jwtbody + "." + jwtsign);
				authCookie.setPath("/");
				String domainName = request.getServerName();
				if (StringUtils.isNotBlank(domainName)) {
					String[] ds = domainName.split("\\.");
					int len = ds.length;
					if (len > 2 && !StringUtils.isNumeric(ds[len - 1])) {
						authCookie.setDomain(ds[len - 2] + "." + ds[len - 1]);
					}
				}
				Cookie tenantCookie = new Cookie("codedriver_tenant", tenant);
				tenantCookie.setPath("/");
				response.addCookie(authCookie);
				response.addCookie(tenantCookie);
				// 允许跨域携带cookie
				response.setHeader("Access-Control-Allow-Credentials", "true");
				response.setContentType(Config.RESPONSE_TYPE_JSON);
				returnObj.put("Status", "OK");
				returnObj.put("JwtToken", jwthead + "." + jwtbody + "." + jwtsign);
				response.getWriter().print(returnObj);
			} else {
				throw new UserAuthFailedException();
			}
		} catch (UserAuthFailedException | TenantNotFoundException ex) {
			ReturnJson.error(ex.getMessage(), response);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			ReturnJson.error(ex.getMessage(), response);
		} finally {
			tenantContext.release();
		}
	}
}

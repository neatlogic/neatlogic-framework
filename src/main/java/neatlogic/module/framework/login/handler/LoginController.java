/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.module.framework.login.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.init.MaintenanceMode;
import neatlogic.framework.common.ReturnJson;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.DeviceType;
import neatlogic.framework.common.util.CommonUtil;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.dao.mapper.*;
import neatlogic.framework.dto.*;
import neatlogic.framework.dto.captcha.LoginCaptchaVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.login.LoginAuthPluginNoFoundException;
import neatlogic.framework.exception.tenant.TenantNotFoundException;
import neatlogic.framework.exception.tenant.TenantUnActiveException;
import neatlogic.framework.exception.user.UserAuthFailedException;
import neatlogic.framework.exception.user.UserNotFoundException;
import neatlogic.framework.filter.core.ILoginAuthHandler;
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.login.core.ILoginPostProcessor;
import neatlogic.framework.login.core.LoginPostProcessorFactory;
import neatlogic.framework.service.AuthenticationInfoService;
import neatlogic.framework.service.TenantService;
import neatlogic.framework.util.CaptchaUtil;
import neatlogic.framework.util.Md5Util;
import neatlogic.framework.util.UuidUtil;
import neatlogic.module.framework.service.LoginService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/login/")
public class LoginController {
    //记录当天(重启)第一次登录时间,用于更新租户最近访问时间
    public static Set<String> tenantVisitSet = new HashSet<>();
    Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserSessionMapper userSessionMapper;

    @Resource
    private UserSessionContentMapper userSessionContentMapper;

    @Resource
    private LoginMapper loginMapper;

    @Resource
    private TenantService tenantService;

    @Resource
    private LoginService loginService;

    @Resource
    private AuthenticationInfoService authenticationInfoService;

    @Resource
    private TenantMapper tenantMapper;

    @RequestMapping(value = "/check/{tenant}")
    public void dispatcherForPost(@RequestBody String json, @PathVariable("tenant") String tenant,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject returnObj = new JSONObject();
        JSONObject jsonObj = JSON.parseObject(json);
        TenantContext tenantContext = TenantContext.init();
        //初始化request上下文
        RequestContext.init(request, request.getRequestURI(), response);
        JSONObject resultJson = new JSONObject();
        try {
            String userId = jsonObj.getString("userid");
            if (StringUtils.isBlank(userId)) {
                throw new UserNotFoundException(userId);
            }
            String password = jsonObj.getString("password");
            String authType = "default";
            if (StringUtils.isNotBlank(jsonObj.getString("authType"))) {
                authType = jsonObj.getString("authType");
            } else if (StringUtils.isNotBlank(Config.LOGIN_AUTH_TYPE())) {
                authType = Config.LOGIN_AUTH_TYPE();
            }

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
            // 验证并获取用户
            UserVo userVo = new UserVo();
            userVo.setUserId(userId);
            userVo.setPassword(password);
            userVo.setTenant(tenant);

            UserVo checkUserVo = null;
            AuthenticationInfoVo authenticationInfoVo = null;
            if (Config.ENABLE_MAINTENANCE() && Config.MAINTENANCE().equals(userVo.getUserId())) {
                String maintenancePassword = Config.MAINTENANCE_PASSWORD();
                maintenancePassword = RC4Util.decrypt(maintenancePassword);
                if (Objects.equals(Config.LOGIN_AUTH_PASSWORD_ENCRYPT(), "md5")) {
                    maintenancePassword = "{MD5}" + Md5Util.encryptMD5(maintenancePassword);
                } else if (Objects.equals(Config.LOGIN_AUTH_PASSWORD_ENCRYPT(), "base64")) {
                    maintenancePassword = "{BS}" + new String(Base64.getEncoder().encode(maintenancePassword.getBytes()));
                }
                if (password.equals(maintenancePassword)) {
                    checkUserVo = MaintenanceMode.getMaintenanceUser();
                    authenticationInfoVo = new AuthenticationInfoVo(checkUserVo.getUuid());
                }
            } else {
                if (Config.ENABLE_NO_SECRET()) {
                    checkUserVo = userMapper.getActiveUserByUserId(userVo);
                } else {
                    //目前仅先校验移动端
                    if (Objects.equals(CommonUtil.getDevice(), DeviceType.MOBILE.getValue())) {
                        loginService.loginCaptchaValid(jsonObj, resultJson);
                    }
                    //切换到具体的认证插件
                    ILoginAuthHandler loginAuth = LoginAuthFactory.getLoginAuth(authType);
                    if (loginAuth == null) {//配置了插件，但不在已有的插件范围内
                        throw new LoginAuthPluginNoFoundException();
                    }
                    checkUserVo = loginAuth.login(userVo, returnObj);
                }
                if (checkUserVo != null) {
                    String timezone = "+8:00";
                    authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(checkUserVo.getUuid());
                    UserContext.init(checkUserVo, authenticationInfoVo, timezone, request, response);
                    for (ILoginPostProcessor loginPostProcessor : LoginPostProcessorFactory.getLoginPostProcessorSet()) {
                        loginPostProcessor.loginAfterInitialization();
                    }
                }
            }

            if (checkUserVo != null) {
                checkUserVo.setTenant(tenant);
                JwtVo jwtVo = LoginAuthHandlerBase.buildJwt(checkUserVo, authenticationInfoVo);
                String authenticationInfoStr = null;
                String authInfoHash = null;
                if (authenticationInfoVo != null && (CollectionUtils.isNotEmpty(authenticationInfoVo.getUserUuidList()) || CollectionUtils.isNotEmpty(authenticationInfoVo.getTeamUuidList()) || CollectionUtils.isNotEmpty(authenticationInfoVo.getRoleUuidList()))) {
                    authenticationInfoVo.setHeaderSet(null);
                    authenticationInfoStr = JSON.toJSONString(authenticationInfoVo);
                    if (StringUtils.isNotBlank(authenticationInfoStr)) {
                        authInfoHash = Md5Util.encryptMD5(authenticationInfoStr);
                        userSessionContentMapper.insertUserSessionContent(new UserSessionContentVo(authInfoHash, authenticationInfoStr));
                    }
                }
                // 保存 user 登录访问时间
                userSessionMapper.insertUserSession(checkUserVo.getUuid(), jwtVo.getTokenHash(), jwtVo.getTokenCreateTime(), authInfoHash);
                userSessionContentMapper.insertUserSessionContent(new UserSessionContentVo(jwtVo.getTokenHash(), jwtVo.getToken()));
                //更新租户visitTime
                TenantContext.get().setUseDefaultDatasource(true);
                if (!tenantVisitSet.contains(tenant)) {
                    tenantMapper.updateTenantVisitTime(tenant);
                    tenantVisitSet.add(tenant);
                }
                LoginAuthHandlerBase.setResponseAuthCookie(response, request, tenant, jwtVo);
                returnObj.put("Status", "OK");
                returnObj.put("JwtToken", jwtVo.getJwthead() + "." + jwtVo.getJwtbody() + "." + jwtVo.getJwtsign());
                response.getWriter().print(returnObj);
            } else {
                throw new UserAuthFailedException();
            }
        } catch (ApiRuntimeException ex) {
            ReturnJson.error(ex.getMessage(), resultJson, response);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ReturnJson.error(ex.getMessage(), response);
        } finally {
            tenantContext.release();
            if (UserContext.get() != null) {
                UserContext.get().release();
            }
        }
    }

    @RequestMapping(value = "/get/captcha/{tenant}")
    public void getCaptcha(@RequestBody String json, @PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObj = JSON.parseObject(json);
        TenantContext tenantContext = TenantContext.init();
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
        String sessionId = jsonObj.getString("sessionId");
        JSONObject result = CaptchaUtil.getCaptcha();
        if (StringUtils.isNotBlank(sessionId)) {
            LoginCaptchaVo loginCaptchaVo = loginMapper.getLoginCaptchaBySessionId(sessionId);
            if (loginCaptchaVo == null) {
                sessionId = UuidUtil.randomUuid();
            }
        } else {
            sessionId = UuidUtil.randomUuid();
        }
        long expiredTime = System.currentTimeMillis() + Config.LOGIN_CAPTCHA_EXPIRED_TIME() * 1000L;
        loginMapper.updateLoginCaptcha(new LoginCaptchaVo(sessionId, result.getString("code"), new Date(expiredTime)));
        result.remove("code");
        result.put("sessionId", sessionId);
        response.getWriter().print(result);
    }

}

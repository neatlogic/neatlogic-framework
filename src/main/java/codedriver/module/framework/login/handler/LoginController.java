/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.login.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.init.MaintenanceMode;
import codedriver.framework.common.ReturnJson;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.DeviceType;
import codedriver.framework.common.util.CommonUtil;
import codedriver.framework.dao.mapper.LoginMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.JwtVo;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.dto.captcha.LoginCaptchaVo;
import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.exception.tenant.TenantNotFoundException;
import codedriver.framework.exception.tenant.TenantUnActiveException;
import codedriver.framework.exception.user.UserAuthFailedException;
import codedriver.framework.filter.core.LoginAuthHandlerBase;
import codedriver.framework.login.core.ILoginPostProcessor;
import codedriver.framework.login.core.LoginPostProcessorFactory;
import codedriver.framework.service.TenantService;
import codedriver.framework.util.UuidUtil;
import codedriver.module.framework.service.LoginService;
import com.alibaba.fastjson.JSONObject;
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
import java.util.Date;
import java.util.Objects;

@Controller
@RequestMapping("/login/")
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private LoginMapper loginMapper;

    @Resource
    private TenantService tenantService;

    @Resource
    private LoginService loginService;

    @RequestMapping(value = "/check/{tenant}")
    public void dispatcherForPost(@RequestBody String json, @PathVariable("tenant") String tenant,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject returnObj = new JSONObject();
        JSONObject jsonObj = JSONObject.parseObject(json);
        TenantContext tenantContext = TenantContext.init();
        JSONObject resultJson = new JSONObject();
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
            // 验证并获取用户
            UserVo userVo = new UserVo();
            userVo.setUserId(userId);
            userVo.setPassword(password);
            userVo.setTenant(tenant);

            UserVo checkUserVo = null;
            if (Config.ENABLE_SUPERADMIN() && MaintenanceMode.MAINTENANCE_USER.equals(userVo.getUserId())) {
                checkUserVo = MaintenanceMode.getMaintenanceUser();
            } else {
                if (Config.ENABLE_NO_SECRET()) {
                    checkUserVo = userMapper.getActiveUserByUserId(userVo);
                } else {
                    //目前仅先校验移动端
                    if (Objects.equals(CommonUtil.getDevice(), DeviceType.MOBILE.getValue())) {
                        loginService.loginCaptchaValid(jsonObj, resultJson);
                    }
                    checkUserVo = loginService.loginWithUserIdAndPassword(userVo, resultJson);
                }
                if (checkUserVo != null) {
                    String timezone = "+8:00";
                    UserContext.init(checkUserVo, timezone, request, response);
                    for (ILoginPostProcessor loginPostProcessor : LoginPostProcessorFactory.getLoginPostProcessorSet()) {
                        loginPostProcessor.loginAfterInitialization();
                    }
                }
            }

            if (checkUserVo != null) {
                checkUserVo.setTenant(tenant);
                // 保存 user 登录访问时间
                userMapper.insertUserSession(checkUserVo.getUuid());
                JwtVo jwtVo = LoginAuthHandlerBase.buildJwt(checkUserVo);
                LoginAuthHandlerBase.setResponseAuthCookie(response, request, tenant, jwtVo);
                returnObj.put("Status", "OK");
                returnObj.put("JwtToken", jwtVo.getJwthead() + "." + jwtVo.getJwtbody() + "." + jwtVo.getJwtsign());
                response.getWriter().print(returnObj);
            } else {
                throw new UserAuthFailedException();
            }
        } catch (ApiRuntimeException ex) {
            ReturnJson.error(ex.getMessage(true), resultJson, response);
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
        JSONObject jsonObj = JSONObject.parseObject(json);
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
        JSONObject result = loginService.getCaptcha();
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

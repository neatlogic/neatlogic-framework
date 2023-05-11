/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

package neatlogic.module.framework.filter.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.utils.StringUtils;
import com.google.common.base.Strings;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.user.UserAuthFailedException;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Service
public class CasLoginAuthHandler extends LoginAuthHandlerBase {

    @Override
    public String getType() {
        return "cas";
    }

    @Override
    public UserVo myAuth(HttpServletRequest request) throws ServletException, IOException{

        String ticket = request.getParameter("ticket");
        String tenant = request.getHeader("Tenant");
        String casUrl = Config.DIRECT_URL();
        String selfUrl = Config.HOME_URL().trim();
        if(!selfUrl.endsWith("/")){
            selfUrl = selfUrl + "/";
        }
        selfUrl = selfUrl + tenant;

        if (StringUtils.isBlank(ticket)) {
            throw new RuntimeException("exception.framework.login.cas.auth.no_ticket");
        }

        if (Strings.isNullOrEmpty(casUrl)) {
            throw new ApiRuntimeException("exception.framework.login.auth.cas.no_config");
        }

        UserVo userVo  = null;
        boolean isFailed = true;
        try {
            String userId = "";
            TicketValidator validator = new Cas20ServiceTicketValidator(casUrl);
            Assertion assertion = validator.validate(ticket, casUrl);
            if (assertion != null && assertion.getPrincipal() != null) {
                if(assertion.getPrincipal().getName().indexOf("@") > -1){
                    userId = assertion.getPrincipal().getName().substring(0, assertion.getPrincipal().getName().indexOf("@")).toUpperCase();
                }else{
                    userId = assertion.getPrincipal().getName().toUpperCase();
                }
            }
            isFailed = true ;
            userVo =  new UserVo();
            userVo.setUserId(userId);
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            userVo = userMapper.getUserByUserId(userId);
            logger.info("[CAS认证成功] userId:" + userId);
        } catch (TicketValidationException e) {
            logger.error("[CAS认证失败]" + e.getMessage());
            isFailed = false;
        }

        if(isFailed){
            throw new UserAuthFailedException();
        }else if(userVo == null){//认证通过，但数据库内没用户
            throw new RuntimeException("exception.framework.login.auth.user_not_found");
        }
        return userVo;
    }

    @Override
    public String myDirectUrl() {
        HttpServletRequest request=  UserContext.get().getRequest();
        String tenant = request.getHeader("Tenant");
        String casUrl = Config.DIRECT_URL();
        String selfUrl = Config.HOME_URL().trim();
        if(!selfUrl.endsWith("/")){
            selfUrl = selfUrl + "/";
        }
        selfUrl = selfUrl + tenant;
        String redirectTo = casUrl + "/login?service=" + Config.HOME_URL() +  selfUrl + "?AuthType=cas";
        return redirectTo;
    }


    @Override
    public UserVo myLogin(UserVo userVo, JSONObject resultJson) {
        return null;
    }

    @Override
    protected void myLogout() throws IOException {
        HttpServletRequest request=  UserContext.get().getRequest();
        HttpServletResponse response = UserContext.get().getResponse();

        String casUrl = Config.DIRECT_URL();
        String selfUrl = Config.HOME_URL().trim();
        String tenant = request.getHeader("Tenant");
        if(!selfUrl.endsWith("/")){
            selfUrl = selfUrl + "/";
        }
        selfUrl = selfUrl + tenant;
        if (Strings.isNullOrEmpty(casUrl)) {
            throw new ApiRuntimeException("exception.framework.login.auth.cas.no_config");
        }
        String redirectTo = casUrl + "/logout?service=" + selfUrl + "&renew=true&other=form";
        response.sendRedirect(redirectTo);
    }
}

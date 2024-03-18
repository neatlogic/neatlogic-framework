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

package neatlogic.module.framework.filter.handler;

import com.alibaba.nacos.api.utils.StringUtils;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.login.LoginAuthCasParamNoFoundException;
import neatlogic.framework.exception.login.LoginAuthConfigNoFoundException;
import neatlogic.framework.exception.login.LoginAuthUserNotFoundException;
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

        String ticket = request.getHeader("AuthValue");
        String tenant = request.getHeader("Tenant");
        String casUrl = Config.DIRECT_URL();
        String selfUrl = Config.HOME_URL().trim();
        if(!selfUrl.endsWith("/")){
            selfUrl = selfUrl + "/";
        }
        selfUrl = selfUrl + tenant;

        if (StringUtils.isBlank(ticket)) {
            throw new LoginAuthCasParamNoFoundException();
        }

        if (StringUtils.isBlank(casUrl)) {
            throw new LoginAuthConfigNoFoundException("cas");
        }

        if(casUrl.indexOf("http:") == -1 ){
            casUrl = "http://" + casUrl;
        }

        UserVo userVo  = null;
        boolean isFailed = true;
        try {
            String userId = "";
            TicketValidator validator = new Cas20ServiceTicketValidator(casUrl);
            Assertion assertion = validator.validate(ticket, selfUrl);
            if (assertion != null && assertion.getPrincipal() != null) {
                if(assertion.getPrincipal().getName().indexOf("@") > -1){
                    userId = assertion.getPrincipal().getName().substring(0, assertion.getPrincipal().getName().indexOf("@")).toUpperCase();
                }else{
                    userId = assertion.getPrincipal().getName().toUpperCase();
                }
            }
            isFailed = false ;
            userVo =  new UserVo();
            userVo.setUserId(userId);
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            userVo = userMapper.getUserByUserId(userId);
            logger.info("[CAS认证成功] userId:" + userId);
        } catch (TicketValidationException e) {
            logger.error("[CAS认证失败]" + e.getMessage());
            isFailed = true;
        }

        if(isFailed){
            throw new UserAuthFailedException();
        }else if(userVo == null){//认证通过，但数据库内没用户
            throw new LoginAuthUserNotFoundException();
        }
        return userVo;
    }

    @Override
    public String myDirectUrl() {
        HttpServletRequest request=  RequestContext.get().getRequest();
        String tenant = request.getHeader("Tenant");
        String casUrl = Config.DIRECT_URL();
        String selfUrl = Config.HOME_URL().trim();
        if(!selfUrl.endsWith("/")){
            selfUrl = selfUrl + "/" ;
        }
        selfUrl = selfUrl + tenant;
        String redirectTo = casUrl + "/login?service=" +  selfUrl;
        return redirectTo;
    }

    @Override
    protected String myLogout() throws IOException {
        HttpServletRequest request=  UserContext.get().getRequest();
        HttpServletResponse response = UserContext.get().getResponse();

        String casUrl = Config.DIRECT_URL();
        String selfUrl = Config.HOME_URL().trim();
        String tenant = request.getHeader("Tenant");
        if(!selfUrl.endsWith("/")){
            selfUrl = selfUrl + "/";
        }
        selfUrl = selfUrl + tenant;
        if (StringUtils.isBlank(casUrl)) {
            throw new LoginAuthConfigNoFoundException("cas");
        }
        String redirectTo = casUrl + "/logout?service=" + selfUrl + "&renew=true&other=form";
        return redirectTo;
    }
}

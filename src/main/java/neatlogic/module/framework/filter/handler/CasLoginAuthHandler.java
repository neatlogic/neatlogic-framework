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
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class CasLoginAuthHandler extends LoginAuthHandlerBase {
    protected static Logger logger = LoggerFactory.getLogger(CasLoginAuthHandler.class);

    @Override
    public String getType() {
        return "cas";
    }

    @Override
    public UserVo myAuth(HttpServletRequest request) throws ServletException, IOException {

        String ticket = request.getHeader("AuthValue");
        String tenant = request.getHeader("Tenant");
        String casUrl = Config.DIRECT_URL();
        String selfUrl = Config.HOME_URL().trim();
        if (!selfUrl.endsWith("/")) {
            selfUrl = selfUrl + "/";
        }
        selfUrl = selfUrl + tenant;
        if (StringUtils.isBlank(ticket)) {
            throw new LoginAuthCasParamNoFoundException();
        }

        if (StringUtils.isBlank(casUrl)) {
            throw new LoginAuthConfigNoFoundException("cas");
        }

        if (!casUrl.contains("http:")) {
            casUrl = "http://" + casUrl;
        }
        logger.debug("=======cas auth ======== ticket:{},tenant:{},casUrl:{},selfUrl:{}", ticket, tenant, casUrl, selfUrl);
        UserVo userVo = null;
        boolean isFailed = true;
        try {
            String userId = "";
            TicketValidator validator = new Cas20ServiceTicketValidator(casUrl);
            Assertion assertion = validator.validate(ticket, selfUrl);
            if (assertion != null && assertion.getPrincipal() != null) {
                if (assertion.getPrincipal().getName().contains("@")) {
                    userId = assertion.getPrincipal().getName().substring(0, assertion.getPrincipal().getName().indexOf("@")).toUpperCase();
                } else {
                    userId = assertion.getPrincipal().getName().toUpperCase();
                }
            }
            isFailed = false;
            userVo = userMapper.getUserByUserId(userId);
            logger.debug("[CAS认证成功] userId:{}", userId);
        } catch (Exception e) {
            logger.error(String.format("[CAS认证失败] ticket:%s,tenant:%s,casUrl:%s,selfUrl:%s,error:%s", ticket, tenant, casUrl, selfUrl, e.getMessage()), e);
        }

        if (isFailed) {
            throw new UserAuthFailedException();
        } else if (userVo == null) {//认证通过，但数据库内没用户
            throw new LoginAuthUserNotFoundException();
        }
        return userVo;
    }

    @Override
    public String myDirectUrl() {
        HttpServletRequest request = RequestContext.get().getRequest();
        String tenant = request.getHeader("Tenant");
        String casUrl = Config.DIRECT_URL();
        String selfUrl = Config.HOME_URL().trim();
        if (!selfUrl.endsWith("/")) {
            selfUrl = selfUrl + "/";
        }
        selfUrl = selfUrl + tenant;
        return casUrl + "/login?service=" + selfUrl;
    }

    @Override
    protected String myLogout() {
        HttpServletRequest request = RequestContext.get().getRequest();

        String casUrl = Config.DIRECT_URL();
        String selfUrl = Config.HOME_URL().trim();
        String tenant = request.getHeader("Tenant");
        if (!selfUrl.endsWith("/")) {
            selfUrl = selfUrl + "/";
        }
        selfUrl = selfUrl + tenant;
        if (StringUtils.isBlank(casUrl)) {
            throw new LoginAuthConfigNoFoundException("cas");
        }
        return casUrl + "/logout?service=" + selfUrl + "&renew=true&other=form";
    }
}

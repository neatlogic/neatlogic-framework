/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.utils.StringUtils;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.auth.AuthEncryptException;
import neatlogic.framework.exception.login.LoginAuthConfigNoFoundException;
import neatlogic.framework.exception.login.LoginAuthUserNotFoundException;
import neatlogic.framework.exception.user.UserAuthFailedException;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.util.$;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Hashtable;

@Service
public class LdapAuthHandler extends LoginAuthHandlerBase {

    @Override
    public String getType() {
        return "ldap";
    }

    @Override
    public UserVo myAuth(HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public String myDirectUrl() {
        return null;
    }

    @Override
    public UserVo myLogin(UserVo userVo, JSONObject resultJson) {

        String userId = userVo.getUserId();
        //支持邮箱登录
        if (userId.contains("@")) {
            userId = userId.substring(0, userId.indexOf("@"));
        }
        String password = userVo.getPassword().replace("{BS}", "");
        byte[] pwdDecode;
        try {
            pwdDecode = Base64.getDecoder().decode(password);
        } catch (Exception ex) {
            throw new AuthEncryptException("base64");
        }
        String credentials = new String(pwdDecode);

        String ldapUrl = Config.LDAP_SERVER_URL();
        String userDn = Config.LDAP_USER_DN().replace("{0}", userId);

        if (StringUtils.isBlank(userDn)) {
            throw new LoginAuthConfigNoFoundException("ldap");
        }

        DirContext dirCtx = null;
        Hashtable<String, String> ldapEnv = new Hashtable<String, String>();
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapEnv.put(Context.PROVIDER_URL, ldapUrl);
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        ldapEnv.put(Context.SECURITY_PRINCIPAL, userDn);
        ldapEnv.put(Context.SECURITY_CREDENTIALS, credentials);
        UserVo checkUserVo = null;
        try {
            dirCtx = new InitialDirContext(ldapEnv);
            checkUserVo = userMapper.getActiveUserByUserId(userVo);
            if (checkUserVo == null) {//认证通过，但数据库内没用户
                throw new LoginAuthUserNotFoundException();
            }
            logger.info($.t("nmffh.ldapauthhandler.mylogin.succeed") + userDn);
        } catch (AuthenticationException e) {
            logger.info($.t("nmffh.ldapauthhandler.mylogin.failed") + userDn + "，" + e.getMessage(), e);
            throw new UserAuthFailedException();
        } catch (NamingException e) {
            logger.error($.t("nmffh.ldapauthhandler.mylogin.faileda") + e.getMessage(), e);
            throw new UserAuthFailedException();
        } finally {
            try {
                if (dirCtx != null) {
                    dirCtx.close();
                }
            } catch (Exception e) {
                logger.error($.t("nmffh.ldapauthhandler.mylogin.faileda") + e.getMessage(), e);
            }
        }
        return checkUserVo;
    }

    @Override
    public boolean isNeedAuth(){
        return false;
    }
}

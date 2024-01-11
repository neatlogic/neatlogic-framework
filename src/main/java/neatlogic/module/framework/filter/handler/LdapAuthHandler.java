/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

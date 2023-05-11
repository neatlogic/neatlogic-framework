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
import com.google.common.base.Strings;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.user.UserAuthFailedException;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
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
        if(userId.indexOf("@") > -1){
            userId = userId.substring(0, userId.indexOf("@"));
        }
        String password = userVo.getPassword().replace("{BS}" , "");
        byte[] pwdDecode = Base64.getDecoder().decode(password);
        String credentials = new String(pwdDecode);

        String ldapUrl = Config.LDAP_SERVER_URL();
        String userDn = Config.LDAP_USER_DN().replace("{0}", userId);

        if(Strings.isNullOrEmpty(ldapUrl) || Strings.isNullOrEmpty(userDn)){
            throw new RuntimeException("exception.framework.login.auth.ldap.no_config");
        }

        DirContext dirCtx = null;
        Hashtable<String, String> ldapEnv = new Hashtable<String, String>();
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapEnv.put(Context.PROVIDER_URL, ldapUrl);
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        ldapEnv.put(Context.SECURITY_PRINCIPAL, userDn);
        ldapEnv.put(Context.SECURITY_CREDENTIALS, credentials);
        UserVo checkUserVo = null ;
        boolean isFailed = true;
        try {
            dirCtx = new InitialDirContext(ldapEnv);
            checkUserVo = userMapper.getActiveUserByUserId(userVo);
            isFailed = false;
            logger.info("[Ldap认证成功]user dn:" + userDn);
        } catch (AuthenticationException e) {
            logger.info("[Ldap认证失败]user dn:" + userDn + "，错误信息:" + e.getMessage());
            isFailed = true ;
        } catch (NamingException e) {
            logger.error("[Ldap认证失败]" + e.getMessage());
            isFailed = true ;
        } finally {
            try {
                dirCtx.close();
            } catch (Exception e) {
                logger.error("[Ldap认证失败]" + e.getMessage());
            }
            dirCtx = null;
        }
        if(isFailed){
            throw new UserAuthFailedException();
        }else if(checkUserVo == null){//认证通过，但数据库内没用户
            throw new RuntimeException("exception.framework.login.auth.user_not_found");
        }
        return checkUserVo;
    }
}

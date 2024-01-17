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

package neatlogic.module.framework.restful.auth.handler;

import neatlogic.framework.exception.integration.AuthenticateException;
import neatlogic.framework.restful.auth.core.ApiAuthBase;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.PublicApiAuthType;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BasicApiAuth extends ApiAuthBase {

    @Override
    public String getType() {
        return PublicApiAuthType.BASIC.getValue();
    }

    @Override
    public int myAuth(ApiVo interfaceVo, JSONObject jsonParam, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(authorization)) {
            authorization = authorization.replace("Basic ", "");
            byte[] bytes = Base64.decodeBase64(authorization);
            authorization = new String(bytes, StandardCharsets.UTF_8);
            String[] as = authorization.split(":");
            if (as.length == 2) {
                String username = as[0];
                String password = as[1];
                if (interfaceVo.getUsername().equalsIgnoreCase(username) && interfaceVo.getPassword().equals(password)) {
                    if (interfaceVo.getTimeout() > 0) {
                        long requestTime = Long.parseLong(request.getHeader("x-access-date"));
                        long timeDiff = System.currentTimeMillis() - requestTime;
                        if (timeDiff < 0 || timeDiff > 1000L * interfaceVo.getTimeout()) {
                            throw new AuthenticateException("请求已超时");
                        }
                    }
                } else {
                    throw new AuthenticateException("用户验证失败");
                }
            } else {
                throw new AuthenticateException("用户验证失败");
            }
        } else {
            throw new AuthenticateException("请求头缺少认证信息");
        }
        return 1;
    }
    
   /* public static void main(String[] arfs) throws UnsupportedEncodingException {
       System.out.println(Base64.encodeBase64String("test:123456".getBytes())); 
       byte[] bytes = Base64.decodeBase64("dGVzdDoxMjM0NTY=");
       String authorization = new String(bytes, StandardCharsets.UTF_8);
       String[] as = authorization.split(":");
       if (as.length == 2) {
           String username = as[0];
           String password = as[1];
           System.out.println(username+":"+password);
       } 
    }*/

    @Override
    public JSONObject help() {
        JSONObject helpJson = new JSONObject();
        helpJson.put("title", "Basic认证");
        List<String> detailList = new ArrayList<>();
        helpJson.put("detailList", detailList);
        detailList.add("request header需要包含键值对：Authorization:Basic xxx");
        detailList.add("xxx是\"用户名:密码\"的BASE64编码");
        return helpJson;
    }

    @Override
    public JSONObject createHeader(JSONObject jsonObj) {
        JSONObject returnObj = new JSONObject();
        String username = jsonObj.getString("username");
        String password = jsonObj.getString("password");
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            String token = Base64.encodeBase64String((username + ":" + password).getBytes());
            returnObj.put("Authorization", "Basic " + token);

        }
        return returnObj;
    }

}

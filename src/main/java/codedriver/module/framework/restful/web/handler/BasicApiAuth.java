/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.restful.web.handler;

import codedriver.framework.restful.dto.ApiVo;
import codedriver.framework.restful.web.core.ApiAuthBase;
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
        return ApiVo.AuthenticateType.BASIC.getValue();
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
                            return 408; //超時
                        }
                    }
                } else {
                    return 401;//用户验证失败
                }
            } else {
                return 401;//用户验证失败
            }
        } else {
            return 412;// 请求头缺少认证信息
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
        detailList.add("request header需要包含键值对Authorization:Basic xxx");
        detailList.add("（xxx是 '用户名:密码' 的base64编码）。");
        return helpJson;
    }

}

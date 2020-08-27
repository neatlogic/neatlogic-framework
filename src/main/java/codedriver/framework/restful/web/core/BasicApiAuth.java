package codedriver.framework.restful.web.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.net.util.Base64;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.restful.dto.ApiVo;

@Service
public class BasicApiAuth extends ApiAuthBase {

    @Override
    public String getType() {
        return ApiVo.AuthenticateType.BASIC.getValue();
    }

    @Override
    public int myAuth(ApiVo interfaceVo, JSONObject jsonParam, HttpServletRequest request) throws IOException {
        String authorization = request.getHeader("Authorization");
        authorization = authorization.replace("Basic ", "");
        byte[] bytes = Base64.decodeBase64(authorization);
        authorization = new String(bytes, "UTF-8");
        String[] as = authorization.split(":");
        if (as.length == 2) {
            String username = as[0];
            String password = as[1];
            if (interfaceVo.getUsername().equalsIgnoreCase(username) && interfaceVo.getPassword().equals(password)) {
                if (interfaceVo.getTimeout() > 0) {
                    long requesttime = Long.parseLong(request.getHeader("x-access-date"));
                    long timediff = System.currentTimeMillis() - requesttime;
                    if (timediff < 0 || timediff > 1000 * interfaceVo.getTimeout()) {
                        return 408; //超時
                    }
                }
            } else {
                return 401;//用户验证失败
            }
        } else {
            return 401;//用户验证失败
        }
        return 1;
    }
    
    public static void main(String[] arfs) throws UnsupportedEncodingException {
       System.out.println(Base64.encodeBase64String("test:123456".getBytes())); 
       byte[] bytes = Base64.decodeBase64("dGVzdDoxMjM0NTY=");
       String authorization = new String(bytes, "UTF-8");
       String[] as = authorization.split(":");
       if (as.length == 2) {
           String username = as[0];
           String password = as[1];
           System.out.println(username+":"+password);
       } 
    }

    @Override
    public JSONObject help() {
        JSONObject helpJson = new JSONObject();
        helpJson.put("title", "Basic认证");
        List<String> detailList = new ArrayList<String>();
        helpJson.put("detailList", detailList);
        detailList.add("request header需要包含键值对Authorization:Basic xxx");
        detailList.add("（xxx是 '用户名:密码' 的base64编码）。");
        return helpJson;
    }

}

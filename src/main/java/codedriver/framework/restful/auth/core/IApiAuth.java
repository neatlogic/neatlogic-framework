package codedriver.framework.restful.auth.core;

import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface IApiAuth {

    public String getType();

    public int auth(ApiVo interfaceVo, JSONObject jsonParam, HttpServletRequest request) throws IOException;
    
    
    public JSONObject help();

}

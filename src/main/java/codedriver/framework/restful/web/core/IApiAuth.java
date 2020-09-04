package codedriver.framework.restful.web.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.restful.dto.ApiVo;

public interface IApiAuth {

    public String getType();

    public int auth(ApiVo interfaceVo, JSONObject jsonParam, HttpServletRequest request) throws IOException;
    
    
    public JSONObject help();

}

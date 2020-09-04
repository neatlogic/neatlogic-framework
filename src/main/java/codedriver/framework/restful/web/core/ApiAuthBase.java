package codedriver.framework.restful.web.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;

public abstract class ApiAuthBase implements IApiAuth {
    protected static ApiMapper apiMapper;

    @Autowired
    public void setApiMapper(ApiMapper _apiMapper) {
        apiMapper = _apiMapper;
    }

    @Override
    public int auth(ApiVo interfaceVo, JSONObject jsonParam, HttpServletRequest request) throws IOException {
        
        return myAuth(interfaceVo,jsonParam,request);
    }

    public abstract int myAuth(ApiVo interfaceVo, JSONObject jsonParam, HttpServletRequest request) throws IOException;

}

package codedriver.framework.restful.web.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiVo;

public abstract class ApiAuthBase implements IApiAuth {
    protected static ApiMapper apiMapper;

    @Autowired
    public void setApiMapper(ApiMapper _apiMapper) {
        apiMapper = _apiMapper;
    }

    @Override
    public int auth(String authorization, String timezone, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        ApiVo apiVo = new ApiVo();
        apiVo.setAuthorization(authorization);
        apiVo.setTimezone(timezone);
        return myAuth(apiVo, null, request, response);
    }

    public abstract int myAuth(ApiVo apiVo, String paramJson, HttpServletRequest request, HttpServletResponse response)
        throws IOException;

}

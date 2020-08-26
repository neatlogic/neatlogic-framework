package codedriver.framework.restful.web.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import codedriver.framework.restful.dto.ApiVo;
@Service
public class BasicApiAuth extends ApiAuthBase {

    @Override
    public String getType() {
        return ApiVo.AuthenticateType.BASIC.getValue();
    }

    @Override
    public int myAuth(ApiVo apiVo, String paramJson, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

}

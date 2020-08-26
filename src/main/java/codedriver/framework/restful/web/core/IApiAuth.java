package codedriver.framework.restful.web.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IApiAuth {

    public String getType();

    public int auth(String authorization, String timezone, HttpServletRequest request, HttpServletResponse response)
        throws IOException;

}

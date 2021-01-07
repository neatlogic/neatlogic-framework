package codedriver.framework.filter.core;

import codedriver.framework.dto.UserVo;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
@Service
public class CasLoginAuthHandler extends LoginAuthHandlerBase {

    @Override
    public String getType() {
        return "cas";
    }

    @Override
    public UserVo myAuth(HttpServletRequest request) throws ServletException, IOException{
       UserVo userVo  = new UserVo();
        return userVo;
    }

    @Override
    public String directUrl() {
        // TODO Auto-generated method stub
        return null;
    }

}

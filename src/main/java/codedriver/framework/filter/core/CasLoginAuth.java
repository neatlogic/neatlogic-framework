package codedriver.framework.filter.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import codedriver.framework.dto.UserVo;
@Service
public class CasLoginAuth extends LoginAuthBase{

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

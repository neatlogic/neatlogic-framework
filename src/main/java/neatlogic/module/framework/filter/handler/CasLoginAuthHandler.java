/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.filter.handler;

import neatlogic.framework.dto.UserVo;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
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

package codedriver.framework.filter.core;

import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.JwtVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class LoginAuthHandlerBase implements ILoginAuthHandler {
    Logger logger = LoggerFactory.getLogger(LoginAuthHandlerBase.class);

    public abstract String getType();

    protected static UserMapper userMapper;

    protected static LoginService loginService;

    @Autowired
    public void setUserMapper(UserMapper _userMapper) {
        userMapper = _userMapper;
    }

    @Autowired
    public void setLoginService(LoginService _loginService) {
        loginService = _loginService;
    }

    @Override
    public UserVo auth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String type = this.getType();
        String tenant = request.getHeader("tenant");
        UserVo userVo = myAuth(request);
        logger.info("loginAuth type: " + type);
        if (userVo != null) {
            logger.info("get userUuId: " + userVo.getUuid());
            logger.info("get userId: " + userVo.getUserId());
        }
        //如果不存在 cookie authorization，则构建jwt,以便下次认证直接走 default 认证
        if (userVo != null && StringUtils.isNotBlank(userVo.getUuid()) && StringUtils.isBlank(userVo.getCookieAuthorization())) {
            JwtVo jwtVo = loginService.buildJwt(userVo);
            loginService.setResponseAuthCookie(response, request, tenant, jwtVo);
        }
        return userVo;
    }

    public abstract UserVo myAuth(HttpServletRequest request) throws ServletException, IOException;


}

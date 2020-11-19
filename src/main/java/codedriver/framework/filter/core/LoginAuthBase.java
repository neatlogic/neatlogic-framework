package codedriver.framework.filter.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.JwtVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.service.LoginService;

public abstract class LoginAuthBase implements ILoginAuth {
	Logger logger = LoggerFactory.getLogger(LoginAuthBase.class);
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
	public UserVo auth(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String type = this.getType();
		String tenant = request.getHeader("tenant");
		UserVo userVo = myAuth(request);
		logger.info("loginAuth type: " + type);
		if(userVo != null) {
			logger.info("get userUuId: " + userVo.getUuid());
			logger.info("get userId: " + userVo.getUserId());
		}
		//如果不存在 authorization，则构建jwt
		if(StringUtils.isBlank(userVo.getAuthorization())) {
    		JwtVo jwtVo = loginService.buildJwt(userVo);
    		Cookie authCookie = new Cookie("codedriver_authorization", "GZIP_" +  jwtVo.getCc());
            authCookie.setPath("/" + tenant);
            String domainName = request.getServerName();
            if (StringUtils.isNotBlank(domainName)) {
                String[] ds = domainName.split("\\.");
                int len = ds.length;
                if (len > 2 && !StringUtils.isNumeric(ds[len - 1])) {
                    authCookie.setDomain(ds[len - 2] + "." + ds[len - 1]);
                }
            }
            Cookie tenantCookie = new Cookie("codedriver_tenant", tenant);
            tenantCookie.setPath("/" + tenant);
            response.addCookie(authCookie);
            response.addCookie(tenantCookie);
            // 允许跨域携带cookie
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setContentType(Config.RESPONSE_TYPE_JSON);
		}
		return userVo;
	}
	
	public abstract UserVo myAuth(HttpServletRequest request) throws ServletException, IOException;
	
	

}

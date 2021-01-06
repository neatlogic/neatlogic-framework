package codedriver.framework.service;

import codedriver.framework.dto.JwtVo;
import codedriver.framework.dto.UserVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginService {
    /**
    * @Author 89770
    * @Time 2020年11月19日  
    * @Description: 构建jwt
    * @Param 
    * @return 
     */
	public JwtVo buildJwt(UserVo checkUserVo) throws Exception ;

	/**
	 * @Description: 设置response cookie
	 * @Author: 89770
	 * @Date: 2021/1/4 16:13
	 * @Params: [response]
	 * @Returns: void
	 **/
	public void setResponseAuthCookie(HttpServletResponse response, HttpServletRequest request, String tenant,JwtVo jwtVo);
}

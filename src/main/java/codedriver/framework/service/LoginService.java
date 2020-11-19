package codedriver.framework.service;

import codedriver.framework.dto.JwtVo;
import codedriver.framework.dto.UserVo;

public interface LoginService {
    /**
    * @Author 89770
    * @Time 2020年11月19日  
    * @Description: 构建jwt
    * @Param 
    * @return 
     */
	public JwtVo buildJwt(UserVo checkUserVo) throws Exception ;
}

package neatlogic.framework.filter.core;

import neatlogic.framework.dto.UserVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ILoginAuthHandler {

    /**
    * @Author 89770
    * @Time 2020年11月19日  
    * @Description: 认证类型
    * @Param 
    * @return
     */
    String getType();
    
    /**
    * @Author 89770
    * @Time 2020年11月19日  
    * @Description: 认证逻辑
    * @Param 
    * @return
     */
    UserVo auth(HttpServletRequest request,HttpServletResponse response) throws Exception;
    
    /**
    * @Author 89770
    * @Time 2020年11月19日  
    * @Description: 跳转url ，如果为null，则跳自带的登录页
    * @Param 
    * @return
     */
    String directUrl();

    /**
     * 登出
     */
    void logout();

}

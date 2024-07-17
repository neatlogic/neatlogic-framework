package neatlogic.framework.filter.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.UserVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ILoginAuthHandler {

    /**
     * 认证类型
     */
    String getType();

    /**
     * 使用场景：接口访问时拦截自定义认证
     *
     * @return 返回的用户对象，必须包含uuid,否则返回的用户无效
     */
    UserVo auth(HttpServletRequest request, HttpServletResponse response) throws Exception;


    /**
     * 使用场景：使用前端登录页面登录
     *
     * @return 返回登录后的用户
     */
    UserVo login(UserVo userVo, JSONObject resultJson);

    /**
     * 使用场景：接口访问时拦截验证失败后，跳转的url
     *
     * @return 系统登出后跳转的url，null则跳转回系统默认登录页面
     */
    String directUrl();

    /**
     * 使用场景：使用登出接口
     *
     * @return 系统登出后跳转的url，null则跳转回系统默认登录页面
     */
    String logout();

    /**
     * 使用场景：移动端接口访问时拦截验证失败后，跳转的url
     *
     * @return 系统登出后跳转的url，null则跳转回系统默认登录页面
     */
    String mobileDirectUrl();

    /**
     * 使用场景：移动端使用登出接口
     *
     * @return 系统登出后跳转的url，null则跳转回系统默认登录页面
     */
    String mobileLogout();

    /**
     * 是否需要更新校验token createTime
     * 默认false
     * <p>
     * 以下情况设置为false：
     * 无需登录的认证类，类似hmac认证这类认证只是一次性认证，应该复用session
     * 生成的token hash无需包含tokenCreateTime，也无需更新tokenCreateTime，否则可能会导致同个用户浏览器登录失效或者每次请求都会生成一个session的问题。
     * <p>
     * 以下情况设置为true：
     * 重写了myLogin方法的认证类，无需复用session
     */
    boolean isValidTokenCreateTime();

    /**
     * 是否需要免登录认证。如果需要，则前端页面会在第一个接口请求走myAuth认证后，才请求后续的接口。
     */
    default boolean isNeedAuth() {
        return true;
    }

}

package neatlogic.framework.filter.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.UserVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ILoginAuthHandler {

    /**
     * @return
     * @Author 89770
     * @Time 2020年11月19日
     * @Description: 认证类型
     * @Param
     */
    String getType();

    /**
     * 资源权限拦截
     *
     * @return
     * @Author 89770
     * @Time 2020年11月19日
     * @Description: 认证逻辑
     * @Param
     */
    UserVo auth(HttpServletRequest request, HttpServletResponse response) throws Exception;


    /**
     * 登录认证
     *
     * @param userVo
     * @param resultJson
     * @return
     */
    UserVo login(UserVo userVo, JSONObject resultJson);

    /**
     * @Author 89770
     * @Time 2020年11月19日
     * @Description: 跳转url ，如果为null，则跳自带的登录页
     * @Param
     */
    String directUrl();

    /**
     * 登出
     */
    String logout();

    /**
     * 是否需要更新校验token createTime
     */
    boolean isValidTokenCreateTime();

    /**
     *
     * 是否需要免登录认证。如果需要，则前端页面会在第一个接口请求走myAuth认证后，才请求后续的接口。
     */
    default boolean isNeedAuth(){
        return true;
    }

}

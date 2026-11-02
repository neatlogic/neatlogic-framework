package neatlogic.framework.filter.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ILoginAuthHandler {

    /**
     * 认证类型
     */
    String getType();

    /**
     * 声明认证处理器能否使用框架统一解析的 multipart 请求，仅影响 multipart 上传的认证阶段。
     *
     * <p>返回 {@code true}：入口向 {@link #auth} 传入 {@code ParsedMultipartRequest}。
     * 首次访问表单字段或文件时才解析 multipart；只读取 Header/Cookie 不触发解析。
     * 认证处理器通过参数、文件接口或 {@code getFormItems()} 获取所需内容，
     * 下游路径过滤器、Dispatcher 和业务复用同一份解析结果，避免认证与业务各解析一次上传。
     * 例如 HMAC 上传使用 {@code getFormItems()} 中的普通字段计算签名，不读取文件内容参与签名。
     * 选择此模式的处理器不得自行再次解析 multipart，也不能依赖重读完整原始 body；
     * 解析后的文件可通过 MultipartFile 重复打开，临时资源由请求入口统一清理。</p>
     *
     * <p>返回 {@code false}（默认）：保留旧认证插件的原始请求读取方式。
     * 认证阶段实际读取原始流，或调用 {@code getParameter*()} 访问 multipart 参数时，
     * 框架缓存完整原始 body，以便后续重放并完成上传解析；仅获取流句柄或读取 Header/Cookie 不缓存。
     * 因此，普通 JWT 上传无需覆盖此方法，也不会因为默认返回 false 而增加 body 缓存。
     * 只有确认上传认证可完全使用共享的字段和文件接口时，才应覆盖并返回 true。</p>
     *
     * <p>此方法不是全局缓存开关：非 multipart 请求不受返回值影响，
     * 仍由框架按实际读取行为及既有表单兼容规则决定是否缓存。</p>
     *
     * @return true 表示接受共享 multipart 解析结果；false 表示保留旧插件的原始请求兼容路径
     */
    default boolean supportsParsedMultipart() {
        return false;
    }

    /**
     * 使用入口已解析的时区完成认证，并在认证成功后初始化用户上下文。
     *
     * @param timezone 已按请求或租户参数解析的时区
     * @return 返回的用户对象，必须包含 uuid，否则返回的用户无效
     */
    UserVo auth(HttpServletRequest request, HttpServletResponse response, String timezone) throws Exception;


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
    String logout(JwtVo jwtVo);

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
     * 是否需要免登录认证。如果需要，则前端页面会在第一个接口请求走myAuth认证后，才请求后续的接口。
     */
    default boolean isNeedAuth() {
        return true;
    }

    /**
     * 是否自动跳转url，需要配合directUrl使用
     */
    default boolean isAutoDirect() {
        return true;
    }

    /**
     * 是否需要补充cookie
     */
    default boolean isNeedCookie() {
        return true;
    }

    /**
     * 检查密码失效
     */
    default boolean checkPwdExpired(UserVo checkUserVo){
        return false;
    }

    /**
     * 密码失效后跳转的 url
     */
    String pwdExpiredDirectUrl();

}

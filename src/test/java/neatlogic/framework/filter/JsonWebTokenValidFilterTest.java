package neatlogic.framework.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.dao.mapper.ConfigMapper;
import neatlogic.framework.dao.mapper.LoginMapper;
import neatlogic.framework.dao.mapper.UserSessionContentMapper;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserSessionVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.user.UserPasswordExpiredException;
import neatlogic.framework.filter.core.ILoginAuthHandler;
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.login.core.ILoginPostProcessor;
import neatlogic.framework.login.core.LoginPostProcessorFactory;
import neatlogic.framework.service.AuthenticationInfoService;
import neatlogic.framework.service.LoginService;
import neatlogic.framework.service.LoginServiceImpl;
import neatlogic.module.framework.filter.handler.DefaultLoginAuthHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * 验证本地会话失效后的第三方认证、失败响应及原有认证边界。
 * 所有会话与外部认证均使用内存替身，真实 JWT 用例复用生产签名和认证基类。
 */
@RunWith(Parameterized.class)
public class JsonWebTokenValidFilterTest {
    private static final String TENANT = "jwt-filter-test";
    private static final String TIMEZONE = "+05:45";
    private static final String BODY = "{\"probe\":true}";
    private static final String AUTH_COOKIE = "neatlogic_authorization";

    private final Scenario scenario;
    private final Map<Field, Object> originalStatics = new LinkedHashMap<>();
    private final Map<ThreadLocal<Object>, Object> originalContexts = new LinkedHashMap<>();
    private final Map<String, UserSessionVo> sessions = new HashMap<>();
    private Map<String, ILoginAuthHandler> authRegistry;
    private Map<String, ILoginAuthHandler> originalHandlers;
    private Set<String> tenants;
    private boolean tenantAlreadyPresent;
    private Set<ILoginPostProcessor> originalPostProcessors;
    private Map<String, String> originalMdc;

    private UserVo localUser;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private int defaultCalls;
    private int thirdPartyCalls;
    private int logoutCalls;
    private int deleteCalls;
    private int chainCalls;
    private int loginPostCalls;
    private JwtVo logoutJwt;

    /** 每个参数组合独立执行并报告，单个场景失败不阻止其他场景运行。 */
    public JsonWebTokenValidFilterTest(Scenario scenario) {
        this.scenario = scenario;
    }

    /** 保留业务场景名称，避免用无含义的布尔参数排列定义测试。 */
    public static final class Scenario {
        private final String name;
        // default 认证状态：valid=会话有效，expired=空闲超时，missing=会话不存在，
        // mismatch=JWT 与会话创建时间不一致（被挤退），anonymous=未识别出用户，
        // password=密码过期，business=业务异常，exception=未知异常。
        private final String state;
        // HTTP 响应状态码：200=放行，500=未知异常，520=业务异常，521=租户不存在，
        // 522=认证失败，525=认证插件不存在，527=会话失效，533=密码过期。
        private final int expectedStatus;
        // 请求头 AuthType；为空时使用 configuredAuthType 模拟的登录认证配置。
        private String authType;
        private String configuredAuthType = "sso";
        // 第三方认证结果：success=返回有效用户，empty=返回 null，invalid=返回的用户缺少 UUID，
        // business=抛出已知业务异常，exception=抛出未知异常。
        private String outcome = "success";
        private boolean autoDirect = true;
        private boolean validTenant = true;
        private boolean chainError;
        private boolean mcp;
        private boolean realJwt;
        private boolean headerJwt;
        private boolean remoteUser;

        /** 默认使用第三方认证成功的普通请求，特殊行为由对应场景显式设置。 */
        private Scenario(String name, String state, int expectedStatus) {
            this.name = name;
            this.state = state;
            this.expectedStatus = expectedStatus;
        }

        /** 将参数名称展示在 JUnit 报告中，便于定位失败场景。 */
        @Override
        public String toString() {
            return name;
        }
    }

    /** 覆盖 52 个过滤器分支场景及 13 个真实 JWT 与认证基类场景。 */
    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> scenarios() {
        List<Scenario> cases = new ArrayList<>();
        // 场景组一（21 项）：default 会话有效时直接放行，不调用第三方；
        // 超时、会话不存在、被挤退或未识别出用户时尝试第三方，成功后放行且不登出。
        // 第三方返回空用户或缺少 UUID 时返回 522；业务异常返回 520，未知异常返回 500。
        for (String state : List.of("valid", "expired", "missing", "mismatch", "anonymous")) {
            cases.add(new Scenario(state + "/sso-success", state, 200));
            if (!"valid".equals(state)) {
                for (String outcome : List.of("empty", "invalid", "business", "exception")) {
                    Scenario item = new Scenario(state + "/sso-" + outcome, state, failureStatus(outcome));
                    item.outcome = outcome;
                    cases.add(item);
                }
            }
        }
        // 场景组二（12 项）：请求头显式指定 default，覆盖三种大小写写法。
        // 即使配置为 sso，也不能再次执行 default 或转用 sso；已失效用户登出并返回 527，
        // 未识别出用户时返回 522，防止重复 default 认证绕过会话失效判断。
        for (String authType : List.of("default", "DEFAULT", "Default")) {
            for (String state : List.of("expired", "missing", "mismatch", "anonymous")) {
                Scenario item = new Scenario(state + "/header-" + authType, state,
                        "anonymous".equals(state) ? 522 : 527);
                item.authType = authType;
                cases.add(item);
            }
        }
        // 场景组三（10 项）：未传 AuthType，登录认证配置为空、default 或不存在的插件。
        // 空配置及 default 不重复认证：超时返回 527，无用户返回 522；插件不存在统一返回 525。
        for (String configuredType : List.of("", "default", "DEFAULT", "Default", "unknown")) {
            for (String state : List.of("expired", "anonymous")) {
                Scenario item = new Scenario(state + "/config-" + configuredType, state,
                        "unknown".equals(configuredType) ? 525 : "anonymous".equals(state) ? 522 : 527);
                item.configuredAuthType = configuredType;
                cases.add(item);
            }
        }
        // 请求头优先级：配置的插件不存在，但请求头指定有效 sso，仍应认证成功并返回 200。
        Scenario headerOverride = new Scenario("header-overrides-config", "expired", 200);
        headerOverride.authType = "sso";
        headerOverride.configuredAuthType = "unknown";
        cases.add(headerOverride);
        // 手动登录（2 项）：第三方返回空用户且禁用自动跳转，返回 522 并清 Cookie，
        // 同时保留 DirectUrl 和 IsAutoDirect=false，由前端按手动登录流程处理。
        for (String state : List.of("expired", "anonymous")) {
            Scenario item = new Scenario(state + "/manual-login", state, 522);
            item.outcome = "empty";
            item.autoDirect = false;
            cases.add(item);
        }
        // 密码过期仍返回 533，不尝试第三方、不执行登出，也不清除认证 Cookie。
        cases.add(new Scenario("password-expired", "password", 533));
        // default 自身抛业务异常或未知异常时保持 520/500，不将异常当作会话失效继续认证。
        cases.add(new Scenario("default-business-error", "business", 520));
        cases.add(new Scenario("default-unexpected-error", "exception", 500));
        // 租户不存在时返回 521，default、第三方及业务链均不得执行。
        Scenario unknownTenant = new Scenario("unknown-tenant", "valid", 521);
        unknownTenant.validTenant = false;
        cases.add(unknownTenant);
        // 认证已成功后业务链抛异常，返回 520，不误判为认证失败，不登出或清 Cookie。
        Scenario downstreamError = new Scenario("downstream-error", "valid", 520);
        downstreamError.chainError = true;
        cases.add(downstreamError);
        // MCP 请求由专用入口认证；即使当前请求租户无效，也应跳过本过滤器并进入后续链。
        Scenario mcp = new Scenario("mcp-bypass", "expired", 200);
        mcp.validTenant = false;
        mcp.mcp = true;
        cases.add(mcp);

        // 真实 JWT 场景组（10 项）：覆盖 Cookie 与 Authorization Header 两种凭据入口。
        // 有效会话沿用 default；失效 Cookie 经第三方认证重新签发 Cookie；无用户由第三方认证。
        // Header 入口在会话缺失或创建时间不匹配时，会由原有认证基类直接建立或更新会话，
        // 无需第三方；空闲超时仍尝试第三方。这些用例用于保留 Header 入口的既有行为。
        for (String state : List.of("valid", "expired", "missing", "mismatch", "anonymous")) {
            for (boolean header : List.of(false, true)) {
                Scenario item = new Scenario("jwt/" + state + (header ? "/header" : "/cookie"), state, 200);
                item.realJwt = true;
                item.headerJwt = header;
                cases.add(item);
            }
        }
        // 真实 JWT 失败场景（2 项）：超时 Cookie 通过签名校验后，第三方返回空用户为 522，
        // 抛业务异常为 520；均不得放行业务请求，也不额外触发第三方注销。
        for (String outcome : List.of("empty", "business")) {
            Scenario item = new Scenario("jwt/expired/" + outcome, "expired", failureStatus(outcome));
            item.realJwt = true;
            item.outcome = outcome;
            cases.add(item);
        }
        // 身份切换：旧 Cookie 属于 local，第三方确认的用户为 remote；放行时上下文和新 Cookie
        // 均必须属于 remote，不能继续使用失效 Cookie 中的旧身份。
        Scenario remoteUser = new Scenario("jwt/expired/different-user", "expired", 200);
        remoteUser.realJwt = true;
        remoteUser.remoteUser = true;
        cases.add(remoteUser);
        List<Object[]> parameters = new ArrayList<>();
        for (Scenario item : cases) {
            parameters.add(new Object[]{item});
        }
        return parameters;
    }

    /** 认证失败继续使用原有错误协议，业务异常和未知异常不强制转换为登出。 */
    private static int failureStatus(String outcome) {
        return "business".equals(outcome) ? 520 : "exception".equals(outcome) ? 500 : 522;
    }

    /** 隔离静态注册表、配置和线程上下文，不依赖运行中的应用或真实租户。 */
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        originalMdc = MDC.getCopyOfContextMap();
        for (Class<?> type : List.of(UserContext.class, RequestContext.class, TenantContext.class)) {
            ThreadLocal<Object> context = (ThreadLocal<Object>) field(type, "instance").get(null);
            originalContexts.put(context, context.get());
            context.remove();
        }
        authRegistry = (Map<String, ILoginAuthHandler>) field(LoginAuthFactory.class, "loginAuthMap").get(null);
        originalHandlers = new HashMap<>(authRegistry);
        authRegistry.clear();
        tenants = (Set<String>) field(TenantUtil.class, "tenantSet").get(null);
        tenantAlreadyPresent = tenants.contains(TENANT);
        TenantUtil.addTenant(TENANT);
        originalPostProcessors = new HashSet<>(LoginPostProcessorFactory.getLoginPostProcessorSet());
        LoginPostProcessorFactory.getLoginPostProcessorSet().clear();
        LoginPostProcessorFactory.getLoginPostProcessorSet().add(() -> loginPostCalls++);

        replaceStatic(Config.class, "LOGIN_AUTH_TYPE", scenario.configuredAuthType);
        replaceStatic(Config.class, "USER_EXPIRETIME", "1");
        replaceStatic(Config.class, "ENABLE_NO_SECRET", false);
        replaceStatic(Config.class, "ENABLE_CONCURRENT_SESSION", false);
        replaceStatic(Config.class, "JWT_SECRET", "test-only-jwt-filter-secret");
        replaceStatic(Config.class, "HEADER_RULE_PREFIX", "x-probe-");
        replaceStatic(ConfigManager.class, "configMapper", proxy(ConfigMapper.class,
                (p, method, args) -> defaultValue(method)));

        TenantContext.init(TENANT);
        request = new MockHttpServletRequest("POST", scenario.mcp ? "/api/mcp/probe" : "/api/rest/probe");
        request.addHeader("Tenant", scenario.validTenant ? TENANT : "unknown-jwt-filter-tenant");
        if (scenario.authType != null) {
            request.addHeader("AuthType", scenario.authType);
        }
        request.setContentType("application/json");
        request.setContent(BODY.getBytes(StandardCharsets.UTF_8));
        request.setCookies(new Cookie("neatlogic_timezone", TIMEZONE));
        response = new MockHttpServletResponse();
        RequestContext.init(request, request.getRequestURI(), response);
        localUser = user("local");
        if (scenario.realJwt) {
            JwtVo jwt = LoginAuthHandlerBase.buildJwt(localUser, new AuthenticationInfoVo(), null);
            localUser.setJwtVo(jwt);
            if (!"anonymous".equals(scenario.state)) {
                if (scenario.headerJwt) {
                    request.addHeader("Authorization", localUser.getAuthorization());
                } else {
                    request.setCookies(new Cookie("neatlogic_timezone", TIMEZONE),
                            new Cookie(AUTH_COOKIE, "GZIP_" + jwt.getCc()));
                }
            }
        } else {
            localUser.setCookieAuthorization("test-cookie");
        }
        if (!"missing".equals(scenario.state) && !"anonymous".equals(scenario.state)) {
            UserSessionVo session = new UserSessionVo();
            session.setUserUuid(localUser.getUuid());
            session.setTokenHash(localUser.getJwtVo().getTokenHash());
            session.setTokenCreateTime(localUser.getJwtVo().getTokenCreateTime()
                    + ("mismatch".equals(scenario.state) ? 100000L : 0L));
            // 与超时阈值留足间隔，避免测试速度或调度抖动影响结果。
            session.setSessionTime(new Date(System.currentTimeMillis()
                    - ("expired".equals(scenario.state) ? 300000L : 0L)));
            session.setAuthInfoHash("auth");
            sessions.put(session.getTokenHash(), session);
        }
    }

    /** 恢复所有替换的全局状态，确保同一 JVM 中其他单元测试不受影响。 */
    @After
    public void tearDown() throws Exception {
        for (Map.Entry<Field, Object> entry : originalStatics.entrySet()) {
            entry.getKey().set(null, entry.getValue());
        }
        if (authRegistry != null && originalHandlers != null) {
            authRegistry.clear();
            authRegistry.putAll(originalHandlers);
        }
        if (tenants != null && !tenantAlreadyPresent) {
            // 仅移除内存测试租户，不调用会卸载真实数据源的 removeTenant。
            tenants.remove(TENANT);
        }
        if (originalPostProcessors != null) {
            LoginPostProcessorFactory.getLoginPostProcessorSet().clear();
            LoginPostProcessorFactory.getLoginPostProcessorSet().addAll(originalPostProcessors);
        }
        for (Map.Entry<ThreadLocal<Object>, Object> entry : originalContexts.entrySet()) {
            if (entry.getValue() == null) {
                entry.getKey().remove();
            } else {
                entry.getKey().set(entry.getValue());
            }
        }
        MDC.clear();
        if (originalMdc != null) {
            MDC.setContextMap(originalMdc);
        }
    }

    /** 对业务可见结果及认证副作用作断言，不要求复制过滤器内部实现。 */
    @Test
    public void preservesAuthenticationAndResponseContract() throws Exception {
        UserSessionMapper sessionMapper = sessionMapper();
        UserSessionContentMapper contentMapper = proxy(UserSessionContentMapper.class,
                (p, method, args) -> "getUserSessionContentByHash".equals(method.getName())
                        ? "{}" : defaultValue(method));
        LoginService service;
        if (scenario.realJwt) {
            installRealHandlers(sessionMapper, contentMapper);
            service = new LoginServiceImpl();
        } else {
            authRegistry.put("DEFAULT", stubHandler(true));
            authRegistry.put("SSO", stubHandler(false));
            service = proxy(LoginService.class, (p, method, args) -> {
                if ("logout".equals(method.getName())) {
                    logoutCalls++;
                    logoutJwt = (JwtVo) args[0];
                    return "/sso-logout";
                }
                return defaultValue(method);
            });
        }
        JsonWebTokenValidFilter filter = new JsonWebTokenValidFilter();
        field(JsonWebTokenValidFilter.class, "userSessionMapper").set(filter, sessionMapper);
        field(JsonWebTokenValidFilter.class, "userSessionContentMapper").set(filter, contentMapper);
        field(JsonWebTokenValidFilter.class, "loginService").set(filter, service);
        FilterChain chain = (q, s) -> {
            chainCalls++;
            assertEquals(BODY, new String(q.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            if (!scenario.mcp) {
                assertNotNull(UserContext.get());
                assertEquals(TIMEZONE, UserContext.get().getTimezone());
            }
            if (scenario.chainError) {
                throw new ServletException("模拟业务链异常");
            }
        };
        filter.doFilter(request, response, chain);
        verifyResult();
    }

    /** 使用内存会话模拟查询、删除、创建与时间更新，覆盖真实认证基类的会话生命周期。 */
    private UserSessionMapper sessionMapper() {
        return proxy(UserSessionMapper.class, (p, method, args) -> {
            switch (method.getName()) {
                case "getUserSessionByTokenHash":
                case "getUserSessionByTokenHashWithoutCache":
                    return sessions.get((String) args[0]);
                case "deleteUserSessionByTokenHash":
                    deleteCalls++;
                    return sessions.remove((String) args[0]) == null ? 0 : 1;
                case "insertUserSession":
                    UserSessionVo session = new UserSessionVo();
                    session.setUserUuid((String) args[0]);
                    session.setTokenHash((String) args[1]);
                    session.setTokenCreateTime((Long) args[2]);
                    session.setAuthInfoHash((String) args[3]);
                    session.setSessionTime(new Date());
                    sessions.put(session.getTokenHash(), session);
                    return 1;
                case "updateUserSessionCreateTime":
                    sessions.get((String) args[1]).setTokenCreateTime((Long) args[0]);
                    return 1;
                default:
                    return defaultValue(method);
            }
        });
    }

    /** 模拟认证插件的成功、空用户及异常结果，并验证缓存请求体和时区被完整传递。 */
    private ILoginAuthHandler stubHandler(boolean local) {
        return proxy(ILoginAuthHandler.class, (p, method, args) -> {
            switch (method.getName()) {
                case "getType":
                    return local ? "default" : "sso";
                case "directUrl":
                    return local ? "/default-login" : "/sso-login";
                case "isAutoDirect":
                    return local || scenario.autoDirect;
                case "auth":
                    assertEquals(TIMEZONE, args[2]);
                    assertEquals(BODY, new String(((HttpServletRequest) args[0]).getInputStream().readAllBytes(),
                            StandardCharsets.UTF_8));
                    if (local) {
                        defaultCalls++;
                        if ("password".equals(scenario.state)) {
                            throw new UserPasswordExpiredException();
                        }
                        failIfRequested(scenario.state);
                        return "anonymous".equals(scenario.state) ? null : localUser;
                    }
                    thirdPartyCalls++;
                    UserVo remote = authenticateThirdParty();
                    if (remote != null && remote.getUuid() != null) {
                        remote.setAuthorization("test-third-party-token");
                        UserContext.init(remote, new AuthenticationInfoVo(), (String) args[2]);
                        Cookie cookie = new Cookie(AUTH_COOKIE, "new-cookie");
                        cookie.setPath("/" + TENANT);
                        ((HttpServletResponse) args[1]).addCookie(cookie);
                    }
                    return remote;
                default:
                    return defaultValue(method);
            }
        });
    }

    /** 保留生产 default 校验及认证基类，仅替换数据库、第三方凭据校验和登录后处理。 */
    private void installRealHandlers(UserSessionMapper sessionMapper,
                                     UserSessionContentMapper contentMapper) throws Exception {
        DefaultLoginAuthHandler defaultHandler = new DefaultLoginAuthHandler() {
            /** 记录真实 default 校验的调用次数，保留签名与凭据解析逻辑。 */
            @Override
            public UserVo myAuth(HttpServletRequest q) throws ServletException, IOException {
                defaultCalls++;
                return super.myAuth(q);
            }

            /** 为测试失败响应提供确定的默认登录地址。 */
            @Override
            public String myDirectUrl() {
                return "/default-login";
            }
        };
        LoginAuthHandlerBase thirdPartyHandler = new LoginAuthHandlerBase() {
            /** 将测试认证插件注册为独立的第三方类型。 */
            @Override
            public String getType() {
                return "sso";
            }

            /** 仅模拟外部凭据校验，后续签发 Cookie 和初始化上下文使用真实基类。 */
            @Override
            public UserVo myAuth(HttpServletRequest q) throws IOException {
                thirdPartyCalls++;
                assertEquals(BODY, new String(q.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
                return authenticateThirdParty();
            }

            /** 为第三方认证失败返回确定的登录地址。 */
            @Override
            public String myDirectUrl() {
                return "/sso-login";
            }

            /** 记录插件注销，不访问任何外部 SSO 服务。 */
            @Override
            protected String myLogout() {
                logoutCalls++;
                return "/sso-logout";
            }
        };
        replaceStatic(LoginAuthHandlerBase.class, "userSessionMapper", sessionMapper);
        replaceStatic(LoginAuthHandlerBase.class, "userSessionContentMapper", contentMapper);
        replaceStatic(LoginAuthHandlerBase.class, "authenticationInfoService",
                proxy(AuthenticationInfoService.class, (p, method, args) ->
                        "getAuthenticationInfo".equals(method.getName())
                                ? new AuthenticationInfoVo() : defaultValue(method)));
        replaceStatic(LoginAuthHandlerBase.class, "loginMapper",
                proxy(LoginMapper.class, (p, method, args) -> defaultValue(method)));
        authRegistry.put("DEFAULT", defaultHandler);
        authRegistry.put("SSO", thirdPartyHandler);
    }

    /** 模拟第三方认证结果，明确允许重新认证成由第三方确认的用户身份。 */
    private UserVo authenticateThirdParty() throws IOException {
        failIfRequested(scenario.outcome);
        if ("empty".equals(scenario.outcome)) {
            return null;
        }
        if ("invalid".equals(scenario.outcome)) {
            return new UserVo();
        }
        return user(!scenario.realJwt || scenario.remoteUser ? "remote" : "local");
    }

    /** 构造业务异常及未知异常，验证过滤器继续使用既有响应协议。 */
    private static void failIfRequested(String outcome) throws IOException {
        if ("business".equals(outcome)) {
            throw new ApiRuntimeException("模拟认证业务异常");
        }
        if ("exception".equals(outcome)) {
            throw new IOException("模拟认证通信异常");
        }
    }

    /** 校验状态码、身份、Cookie、跳转协议及是否执行登出或业务链。 */
    private void verifyResult() throws Exception {
        assertEquals(scenario.expectedStatus, response.getStatus());
        assertEquals(scenario.mcp || !scenario.validTenant ? 0 : 1, defaultCalls);
        assertEquals(scenario.expectedStatus == 200 || scenario.chainError ? 1 : 0, chainCalls);
        boolean defaultRejected = List.of("password", "business", "exception").contains(scenario.state);
        // 真实 Header 认证会先由基类补建或更新会话，与失效 Cookie 进入第三方认证的路径不同。
        boolean headerHandledLocally = scenario.realJwt && scenario.headerJwt
                && List.of("missing", "mismatch").contains(scenario.state);
        boolean thirdPartyExpected = scenario.validTenant && !scenario.mcp
                && !"valid".equals(scenario.state) && !defaultRejected && !headerHandledLocally
                && "sso".equalsIgnoreCase(scenario.authType == null
                ? scenario.configuredAuthType : scenario.authType);
        assertEquals(thirdPartyExpected ? 1 : 0, thirdPartyCalls);
        assertEquals(scenario.expectedStatus == 527 ? 1 : 0, logoutCalls);
        if (logoutCalls > 0) {
            assertSame(localUser.getJwtVo(), logoutJwt);
        }
        if ("missing".equals(scenario.state) || "mismatch".equals(scenario.state)) {
            assertEquals(0, deleteCalls);
        }
        if ("expired".equals(scenario.state) && !scenario.mcp) {
            assertEquals(1, deleteCalls);
        }
        List<Cookie> authCookies = new ArrayList<>();
        for (Cookie cookie : response.getCookies()) {
            if (AUTH_COOKIE.equals(cookie.getName())) {
                authCookies.add(cookie);
            }
        }
        long cleared = authCookies.stream().filter(c -> c.getMaxAge() == 0).count();
        boolean clearExpected = List.of(521, 522, 525, 527).contains(scenario.expectedStatus);
        assertEquals(clearExpected ? 1L : 0L, cleared);
        JSONObject json = JSON.parseObject(response.getContentAsString());
        if (scenario.expectedStatus != 200) {
            assertEquals("FAILED", json.getString("Status"));
            assertFalse(json.getString("Message").isBlank());
            assertEquals(thirdPartyCalls > 0 ? "sso" : "default", json.getString("authType"));
            assertEquals(thirdPartyCalls > 0 ? "/sso-login" : "/default-login", json.getString("DirectUrl"));
            assertEquals(thirdPartyCalls == 0 || scenario.autoDirect, json.getBooleanValue("IsAutoDirect"));
        } else if (!scenario.mcp) {
            assertEquals(thirdPartyCalls > 0 && (!scenario.realJwt || scenario.remoteUser)
                    ? "remote" : "local", UserContext.get().getUserUuid(true));
            if (thirdPartyCalls > 0) {
                assertFalse(authCookies.isEmpty());
                if (scenario.realJwt) {
                    verifyIssuedCookie(authCookies.get(authCookies.size() - 1));
                }
            }
        }
        // 被挤退后可复用现有会话，不要求重新执行登录后处理；其余第三方成功场景创建新会话，
        // 应执行一次登录后处理，避免遗漏或重复触发。
        if (scenario.realJwt && thirdPartyCalls > 0 && scenario.expectedStatus == 200
                && !"mismatch".equals(scenario.state)) {
            assertEquals(1, loginPostCalls);
        }
    }

    /** 验证重新签发的 Cookie 可由真实 default 校验解析，且与新会话一致。 */
    private void verifyIssuedCookie(Cookie cookie) throws Exception {
        assertEquals("/" + TENANT, cookie.getPath());
        assertTrue(cookie.getValue().startsWith("GZIP_"));
        assertNotEquals(0, cookie.getMaxAge());
        MockHttpServletRequest next = new MockHttpServletRequest("POST", "/api/rest/probe");
        next.addHeader("Tenant", TENANT);
        next.setCookies(new Cookie("neatlogic_timezone", TIMEZONE), cookie);
        UserVo parsed = new DefaultLoginAuthHandler().myAuth(next);
        assertEquals(UserContext.get().getUserUuid(true), parsed.getUuid());
        UserSessionVo session = sessions.get(parsed.getJwtVo().getTokenHash());
        assertNotNull(session);
        assertTrue(parsed.getJwtVo().validTokenCreateTime(session.getTokenCreateTime()));
    }

    /** 构造确定的测试身份，普通替身凭据不承担真实签名校验。 */
    private static UserVo user(String id) {
        UserVo user = new UserVo();
        user.setUuid(id);
        user.setUserId(id);
        user.setUserName(id);
        user.setTenant(TENANT);
        JwtVo jwt = new JwtVo();
        jwt.setToken("test-token-" + id);
        jwt.setTokenCreateTime(1000L);
        user.setJwtVo(jwt);
        return user;
    }

    /** 获取非公开字段，仅供隔离现有静态依赖及注入测试替身。 */
    private static Field field(Class<?> type, String name) throws NoSuchFieldException {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /** 每个静态字段只保存一次原值，避免重复设置后无法恢复。 */
    private void replaceStatic(Class<?> type, String name, Object value) throws Exception {
        Field field = field(type, name);
        if (!originalStatics.containsKey(field)) {
            originalStatics.put(field, field.get(null));
        }
        field.set(null, value);
    }

    /** 使用现有 JDK 动态代理模拟接口，不引入额外测试依赖。 */
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler));
    }

    /** 为不参与本次认证的 Mapper 或插件方法返回无副作用的基础值。 */
    private static Object defaultValue(Method method) {
        if (method.getReturnType() == int.class) {
            return 0;
        }
        if (method.getReturnType() == long.class) {
            return 0L;
        }
        if (method.getReturnType() == boolean.class) {
            return false;
        }
        return null;
    }
}

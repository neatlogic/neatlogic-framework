/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.filter.request;

import static neatlogic.framework.filter.request.RequestBodyTestSupport.isBodyCached;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.dao.mapper.ConfigMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dao.mapper.UserSessionContentMapper;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserSessionVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.filter.JsonWebTokenValidFilter;
import neatlogic.framework.filter.PathTraversalFilter;
import neatlogic.framework.filter.core.ILoginAuthHandler;
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.restful.constvalue.ApiAuthType;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.core.privateapi.binarystream.IBinaryStreamApiComponent;
import neatlogic.framework.restful.core.privateapi.jsonstream.IJsonStreamApiComponent;
import neatlogic.framework.restful.core.privateapi.raw.IRawApiComponent;
import neatlogic.framework.restful.core.privateapi.sse.IPrivateSseApiComponent;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiHandlerVo;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.restful.ratelimiter.RateLimiterTokenBucket;
import neatlogic.framework.service.UserService;
import neatlogic.framework.util.SHA256Util;
import neatlogic.module.framework.filter.handler.BasicAuthHandler;
import neatlogic.module.framework.filter.handler.BearerTokenAuthHandler;
import neatlogic.module.framework.filter.handler.DefaultLoginAuthHandler;
import neatlogic.module.framework.filter.handler.HmacLoginAuthHandler;
import neatlogic.module.framework.restful.counter.ApiAccessCountService;
import neatlogic.module.framework.restful.dispatch.handler.ApiDispatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** 真实 JWT 过滤器、路径过滤器、Spring 参数绑定和 ApiDispatcher 的集成测试；数据库与业务组件使用内存替身。 */
public abstract class RequestBodyMvcFixture {
    protected static final String TENANT = "request-body-dispatch-test";
    protected static final String TOKEN = "request-body-probe";
    @Rule public TemporaryFolder directory = new TemporaryFolder();
    protected final List<Runnable> restore = new ArrayList<>();
    protected MockMvc mvc;
    protected JsonWebTokenValidFilter filter;
    protected boolean omitJwtCookie;
    protected boolean failBusiness;
    protected boolean cached;
    protected int itemCount;
    protected int calls;
    protected JSONObject received;
    protected UserVo user;
    protected String jwtCookie;
    protected String jwtHeader;
    protected Business business;
    protected int temporaryFiles;
    protected long temporaryBytes;

    /** 在真实分发终点接入所属模块业务，不替换认证、解析和 Dispatcher。 */
    @FunctionalInterface
    protected interface Business {
        /** 调用业务组件并返回实际结果，由测试捕获存储和参数证据。 */
        Object execute(JSONObject parameters, HttpServletRequest request, HttpServletResponse response) throws Exception;
    }

    /** 安装独立注册项；不启动真实模块、不连接数据库或修改租户配置。 */
    @Before public void setUp() throws Exception {
        setStatic(Config.class, "HEADER_RULE_PREFIX", "x-test-");
        setStatic(Config.class, "JWT_SECRET", "request-body-test-secret");
        setStatic(Config.class, "USER_EXPIRETIME", "60");
        setStatic(Config.class, "LOGIN_AUTH_TYPE", "default");
        setStatic(Config.class, "ENABLE_CONCURRENT_SESSION", false);
        setStatic(Config.class, "ENABLE_NO_SECRET", false);
        setStatic(Config.class, "REQUEST_BODY_MEMORY_THRESHOLD", 64);
        setStatic(Config.class, "REQUEST_BODY_MAX_BYTES", 10485760L);
        setStatic(Config.class, "REQUEST_MULTIPART_MAX_BYTES", 1073741824L);
        setStatic(Config.class, "REQUEST_MULTIPART_MEMORY_THRESHOLD", 64);
        setStatic(Config.class, "REQUEST_MULTIPART_FIELDS_MAX_BYTES", 10485760L);
        setStatic(Config.class, "REQUEST_MULTIPART_PARTS_MAX", 128);
        setStatic(ConfigManager.class, "configMapper", proxy(ConfigMapper.class, (p, m, a) -> fallback(m.getReturnType())));
        Field tenantsField = TenantUtil.class.getDeclaredField("tenantSet"); tenantsField.setAccessible(true);
        @SuppressWarnings("unchecked") Set<String> tenants = (Set<String>) tenantsField.get(null);
        boolean tenantExisted = tenants.contains(TENANT);
        TenantUtil.addTenant(TENANT);
        restore.add(() -> { if (!tenantExisted) { tenants.remove(TENANT); } });
        user = new UserVo(); user.setUuid("body-test-user"); user.setUserId("tester");
        TenantContext.init(TENANT);
        JwtVo jwt = LoginAuthHandlerBase.buildJwt(user, new AuthenticationInfoVo(), "default");
        jwtCookie = "GZIP_" + jwt.getCc();
        jwtHeader = "Bearer " + jwt.getJwthead() + "." + jwt.getJwtbody() + "." + jwt.getJwtsign();
        UserSessionVo session = new UserSessionVo(); session.setTokenHash(jwt.getTokenHash());
        session.setTokenCreateTime(jwt.getTokenCreateTime()); session.setSessionTime(new Date()); session.setAuthInfoHash("body-test-auth");
        UserSessionMapper sessions = proxy(UserSessionMapper.class, (p, m, a) -> {
            if (m.getName().equals("getUserSessionByTokenHash")) { return session; }
            // Header JWT 在认证基类中续签，内存数据库同步保存创建时间，保持与真实 Mapper 相同的读写契约。
            if (m.getName().equals("updateUserSessionCreateTime")) { session.setTokenCreateTime((Long) a[0]); }
            return fallback(m.getReturnType());
        });
        UserSessionContentMapper contents = proxy(UserSessionContentMapper.class, (p, m, a) -> "{}");
        setStatic(LoginAuthHandlerBase.class, "userSessionMapper", sessions);
        setStatic(LoginAuthHandlerBase.class, "userSessionContentMapper", contents);
        filter = new JsonWebTokenValidFilter();
        field(filter, "multipartResolver", new RequestMultipartResolver());
        field(filter, "userSessionMapper", sessions);
        field(filter, "userSessionContentMapper", contents);
        Map<String, ILoginAuthHandler> handlers = map(LoginAuthFactory.class, "loginAuthMap");
        put(handlers, "DEFAULT", new DefaultLoginAuthHandler());
        put(handlers, "HMAC", hmac());
        BearerTokenAuthHandler bearer = new BearerTokenAuthHandler();
        field(bearer, "userMapper", proxy(UserMapper.class, (p, m, a) -> m.getName().equals("getUserByUser") && "opaque-test-token".equals(a[0]) ? user : fallback(m.getReturnType())));
        put(handlers, "BEARERTOKEN", bearer);
        ApiVo api = new ApiVo(); api.setToken(TOKEN); api.setHandler(TOKEN); api.setIsActive(1);
        api.addAuthType(ApiAuthType.BASIC.getValue()); api.setUsername("tester"); api.setPassword("test-password");
        BasicAuthHandler basic = new BasicAuthHandler();
        field(basic, "apiMapper", proxy(ApiMapper.class, (p, m, a) -> m.getName().equals("getApiByToken") ? api : fallback(m.getReturnType())));
        put(handlers, "BASIC", basic);
        put(map(PrivateApiComponentFactory.class, "apiMap"), TOKEN, api);
        ApiHandlerVo metadata = new ApiHandlerVo(); metadata.setModuleId("framework"); metadata.setHandler(TOKEN);
        put(map(PrivateApiComponentFactory.class, "apiHandlerMap"), TOKEN, metadata);
        Map<String, Map<String, Object>> registry = map(PrivateApiComponentFactory.class, "componentRegistryMap");
        for (ApiType type : new ApiType[]{ApiType.OBJECT, ApiType.BINARY, ApiType.STREAM, ApiType.RAW, ApiType.SSE}) {
            Class<?> contract = type == ApiType.OBJECT ? IApiComponent.class : type == ApiType.BINARY ? IBinaryStreamApiComponent.class
                    : type == ApiType.STREAM ? IJsonStreamApiComponent.class : type == ApiType.SSE ? IPrivateSseApiComponent.class : IRawApiComponent.class;
            put(registry.get(type.getValue()), TOKEN, proxy(contract, (p, m, a) -> {
                if (!m.getName().equals("doService")) { return fallback(m.getReturnType()); }
                calls++;
                received = type == ApiType.RAW ? new JSONObject() : (JSONObject) a[1];
                if (type == ApiType.RAW) { received.put("payload", a[1]); }
                HttpServletRequest request = RequestContext.get().getRequest();
                RequestBodyResources resources = (RequestBodyResources) request.getAttribute(RequestBodyResources.ATTRIBUTE);
                cached = isBodyCached(resources); itemCount = resources.items.size();
                if (type == ApiType.STREAM) { received.put("stream", ((JSONReader) a[2]).readObject()); }
                temporaryFiles = directory.getRoot().list().length;
                temporaryBytes = Arrays.stream(directory.getRoot().listFiles()).mapToLong(File::length).sum();
                if (business != null) { return business.execute(received, (HttpServletRequest) a[2], (HttpServletResponse) a[3]); }
                if (type == ApiType.BINARY && a[2] instanceof MultipartHttpServletRequest) {
                    MultipartHttpServletRequest upload = (MultipartHttpServletRequest) a[2];
                    try (InputStream first = upload.getFile("upload").getInputStream();
                         InputStream second = upload.getFile("upload").getInputStream()) {
                        assertArrayEquals(first.readAllBytes(), second.readAllBytes());
                    }
                    assertEquals(2, upload.getParameterValues("value").length);
                }
                if (failBusiness) { throw new ApiRuntimeException("模拟业务失败"); }
                return received;
            }));
        }
        ApiDispatcher dispatcher = new ApiDispatcher();
        field(dispatcher, "apiMapper", proxy(ApiMapper.class, (p, m, a) -> fallback(m.getReturnType())));
        field(dispatcher, "apiAccessCountService", (ApiAccessCountService) token -> { });
        mvc = MockMvcBuilders.standaloneSetup(dispatcher).addFilters(filter, new PathTraversalFilter())
                .setHandlerExceptionResolvers(new RequestBodyErrorHandler()).build();
        mvc.getDispatcherServlet().getServletContext().setAttribute("javax.servlet.context.tempdir", directory.getRoot());
    }

    /** 所有静态替身恢复，避免影响现有认证用例。 */
    @After public void tearDown() {
        for (int i = restore.size() - 1; i >= 0; i--) { restore.get(i).run(); }
        RateLimiterTokenBucket.removeTenant(TENANT);
        if (TenantContext.get() != null) { TenantContext.get().release(); }
        if (UserContext.get() != null) { UserContext.get().release(); }
        if (RequestContext.get() != null) { RequestContext.get().release(); }
    }

    /** 固定 servletPath 以触发生产 Dispatcher 的 Ant 路径提取。 */
    protected MockHttpServletRequestBuilder builder(String kind) {
        String path = "/api/" + kind + "/" + TOKEN;
        MockHttpServletRequestBuilder result = post(path).servletPath(path).header("Tenant", TENANT).cookie(new Cookie("neatlogic_timezone", "+08:00"));
        if (!omitJwtCookie) { result.cookie(new Cookie("neatlogic_authorization", jwtCookie)); }
        return result;
    }

    /** 仅为接口创建无数据库副作用的替身。 */
    @SuppressWarnings("unchecked") protected static <T> T proxy(Class<T> type, InvocationHandler invocation) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, invocation);
    }
    /** 替身没有实现的方法返回其类型的空值。 */
    protected static Object fallback(Class<?> type) {
        if (type == boolean.class) { return false; }
        if (type == int.class) { return 0; }
        if (type == long.class) { return 0L; }
        return null;
    }
    /** 设置当前测试实例依赖。 */
    protected static void field(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name); field.setAccessible(true); field.set(target, value);
    }
    /** 静态替身保存原值，并在用例结束恢复。 */
    protected void setStatic(Class<?> type, String name, Object value) throws Exception {
        Field field = type.getDeclaredField(name); field.setAccessible(true); Object original = field.get(null); field.set(null, value);
        restore.add(() -> { try { field.set(null, original); } catch (IllegalAccessException ex) { LoggerFactory.getLogger(RequestBodyMvcFixture.class).error("Cannot restore test field", ex); throw new AssertionError(ex); } });
    }
    /** 访问已有注册表，仅修改本次测试使用的条目。 */
    @SuppressWarnings("unchecked") protected static <K, V> Map<K, V> map(Class<?> type, String name) throws Exception {
        Field field = type.getDeclaredField(name); field.setAccessible(true); return (Map<K, V>) field.get(null);
    }
    /** 注册项退出测试后恢复，包括原先不存在的情况。 */
    protected <K, V> void put(Map<K, V> map, K key, V value) {
        V old = map.put(key, value);
        restore.add(() -> { if (old == null) { map.remove(key); } else { map.put(key, old); } });
    }
    /** 使用内存用户服务替身，认证及会话流程仍执行，用户与签名密钥来自测试数据。 */
    protected static HmacLoginAuthHandler hmac() throws Exception {
        HmacLoginAuthHandler handler = new HmacLoginAuthHandler();
        UserVo user = new UserVo(); user.setUuid("body-test-user"); user.setUserId("tester");
        Field field = HmacLoginAuthHandler.class.getDeclaredField("userService"); field.setAccessible(true);
        field.set(handler, Proxy.newProxyInstance(UserService.class.getClassLoader(), new Class[]{UserService.class},
                (proxy, method, args) -> method.getName().equals("getUserByUser") ? user
                        : method.getName().equals("getUserTokenByUser") ? "test-secret" : null));
        return handler;
    }

    /** 旧签名格式作为兼容基线，不能改成参数 Map 的序列化。 */
    protected static String signature(HttpServletRequest request, String body) {
        String query = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        return "Hmac " + SHA256Util.encrypt("test-secret", "tester#" + request.getRequestURI() + query + "#"
                + Base64.getEncoder().encodeToString(body.getBytes(StandardCharsets.UTF_8)));
    }
}

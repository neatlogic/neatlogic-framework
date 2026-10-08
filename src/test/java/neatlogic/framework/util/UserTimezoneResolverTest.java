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

package neatlogic.framework.util;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.systemuser.ISystemUser;
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.config.FrameworkTenantConfig;
import neatlogic.framework.dao.mapper.ConfigMapper;
import neatlogic.framework.dto.ConfigVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.store.mysql.NeatLogicBasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** 验证请求时区优先级、用户上下文快照及数据库连接对时区的使用。 */
public class UserTimezoneResolverTest {
    private final Map<String, String> configByTenant = new HashMap<>();
    private Field mapperField;
    private Object previousMapper;
    private int queryCount;
    private boolean failQuery;

    /** 替换配置查询，记录查询次数并模拟配置读取失败。 */
    @Before
    public void installMapper() throws Exception {
        TenantContext.init("tenant-a");
        mapperField = ConfigManager.class.getDeclaredField("configMapper");
        mapperField.setAccessible(true);
        previousMapper = mapperField.get(null);
        ConfigMapper mapper = (ConfigMapper) Proxy.newProxyInstance(ConfigMapper.class.getClassLoader(),
                new Class<?>[]{ConfigMapper.class}, (proxy, method, args) -> {
                    if (!"getConfigByKey".equals(method.getName())) {
                        return null;
                    }
                    queryCount++;
                    assertEquals("tenant.default.timezone", args[0]);
                    if (failQuery) {
                        throw new IllegalStateException("模拟配置查询失败");
                    }
                    String value = configByTenant.get(TenantContext.get().getTenantUuid());
                    if (value == null) {
                        return null;
                    }
                    ConfigVo config = new ConfigVo();
                    config.setValue(value);
                    return config;
                });
        mapperField.set(null, mapper);
    }

    /** 恢复静态依赖并释放线程上下文，避免影响其他测试。 */
    @After
    public void cleanup() throws Exception {
        mapperField.set(null, previousMapper);
        if (UserContext.get() != null) {
            UserContext.get().release();
        }
        TenantContext.get().release();
    }

    /** 错误日志及来源标签随请求语言切换，配置读取失败仍保留异常。 */
    @Test
    public void resolverLogsFollowRequestLanguage() {
        Logger logger = (Logger) LoggerFactory.getLogger(UserTimezoneResolver.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        RequestContext previousContext = RequestContext.get();
        try {
            RequestContext.init((RequestContext) null);
            for (Locale locale : List.of(Locale.ENGLISH, Locale.CHINESE, Locale.ENGLISH)) {
                RequestContext.get().setLocale(locale);
                appender.list.clear();
                configByTenant.put("tenant-a", "-05:00");
                failQuery = false;
                assertEquals("-05:00", UserTimezoneResolver.getRequestTimezone(new CountingRequest("%GG").request));
                assertEquals("-05:00", UserTimezoneResolver.getRequestTimezone(new CountingRequest("invalid").request));
                assertEquals(ZoneOffset.of("-05:00"), UserTimezoneResolver.resolve("invalid"));
                assertEquals(ZoneOffset.of("-05:00"), UserTimezoneResolver.resolve("+05:00:30"));
                configByTenant.put("tenant-a", "invalid");
                assertEquals("+08:00", UserTimezoneResolver.getDefaultTimezone());
                failQuery = true;
                assertEquals("+08:00", UserTimezoneResolver.getDefaultTimezone());
                assertEquals(6, appender.list.size());
                String logs = String.join("\n", appender.list.stream()
                        .map(ILoggingEvent::getFormattedMessage).toList());
                if (Locale.ENGLISH.equals(locale)) {
                    assertTrue(logs.contains("Invalid time zone cookie encoding"));
                    assertTrue(logs.contains("Request time zone is not a valid UTC offset"));
                    assertTrue(logs.contains("Explicit time zone contains a seconds component"));
                    assertTrue(logs.contains("Tenant default time zone is not a valid UTC offset"));
                    assertTrue(logs.contains("Failed to read the tenant default time zone"));
                    assertTrue(logs.chars().noneMatch(c -> c >= 0x4e00 && c <= 0x9fff));
                } else {
                    assertTrue(logs.contains("时区 Cookie 编码无效"));
                    assertTrue(logs.contains("请求时区不是有效 UTC 偏移量"));
                    assertTrue(logs.contains("显式时区包含秒级偏移量"));
                    assertTrue(logs.contains("租户默认时区不是有效 UTC 偏移量"));
                    assertTrue(logs.contains("读取租户默认时区失败"));
                }
                assertTrue(appender.list.get(5).getThrowableProxy() != null);
                assertTrue(!logs.contains("framework.usertimezoneresolver") && !logs.contains("{0}"));
            }
        } finally {
            logger.detachAppender(appender);
            appender.stop();
            RequestContext.get().release();
            if (previousContext != null) {
                RequestContext.init(previousContext);
            }
        }
    }

    /** 有效 Cookie 优先于租户配置，并规范化为分钟精度偏移量。 */
    @Test
    public void validCookieTakesPrecedence() {
        configByTenant.put("tenant-a", "-04:00");
        for (String offset : List.of("+08:00", "-05:00", "+05:30", "+05:45", "+00:00")) {
            CountingRequest rawRequest = new CountingRequest(offset);
            assertEquals(offset, UserTimezoneResolver.getRequestTimezone(rawRequest.request));
            assertEquals(1, rawRequest.cookieReads);

            CountingRequest encodedRequest = new CountingRequest(offset.replace("+", "%2B").replace(":", "%3A"));
            assertEquals(offset, UserTimezoneResolver.getRequestTimezone(encodedRequest.request));
            assertEquals(1, encodedRequest.cookieReads);
        }
        assertEquals(0, queryCount);
    }

    /** 缺失、非法编码、非法偏移及秒级偏移均使用租户默认值。 */
    @Test
    public void invalidCookieUsesTenantDefault() {
        configByTenant.put("tenant-a", "+05:45");
        for (String value : new String[]{null, "", "%GG", "invalid", "+19:00", "+05:00:30"}) {
            CountingRequest fixture = new CountingRequest(value);
            assertEquals("+05:45", UserTimezoneResolver.getRequestTimezone(fixture.request));
            assertEquals(1, fixture.cookieReads);
        }
        assertEquals(6, queryCount);
    }

    /** 参数缺失、清空、非法或读取异常均回退枚举内置默认值。 */
    @Test
    public void unusableConfigUsesEnumDefault() {
        for (String value : new String[]{null, " ", "invalid", "+25:00"}) {
            configByTenant.put("tenant-a", value);
            assertEquals(FrameworkTenantConfig.TENANT_DEFAULT_TIMEZONE.getValue(),
                    UserTimezoneResolver.getDefaultTimezone());
        }
        failQuery = true;
        assertEquals("+08:00", UserTimezoneResolver.getDefaultTimezone());
    }

    /** 无业务租户或使用主数据源时不读取租户参数。 */
    @Test
    public void absentTenantDoesNotQueryConfig() {
        TenantContext.init();
        assertEquals("+08:00", UserTimezoneResolver.getDefaultTimezone());
        TenantContext.init("tenant-a").setUseMasterDatabase(true);
        assertEquals("+08:00", UserTimezoneResolver.getDefaultTimezone());
        assertEquals(0, queryCount);
    }

    /** 显式偏移量由公共解析接口校验，零偏移接受 JDK 的 Z 表示。 */
    @Test
    public void resolveValidatesExistingTimezone() {
        configByTenant.put("tenant-a", "-05:00");
        assertEquals(ZoneOffset.of("+05:30"), UserTimezoneResolver.resolve("+05:30"));
        assertEquals(ZoneOffset.UTC, UserTimezoneResolver.resolve("Z"));
        assertEquals(ZoneOffset.of("-05:00"), UserTimezoneResolver.resolve("invalid"));
    }

    /** 已初始化上下文及其副本保留时区快照，不随租户参数变化。 */
    @Test
    public void contextSnapshotSurvivesTenantAndConfigChanges() {
        configByTenant.put("tenant-a", "-05:00");
        configByTenant.put("tenant-b", "+05:45");
        UserContext original = UserContext.init((UserContext) null);
        assertEquals("-05:00", original.getTimezone());
        int queriesAfterInit = queryCount;

        configByTenant.put("tenant-a", "+00:00");
        assertEquals("-05:00", UserContext.init(original).getTimezone());
        assertEquals(queriesAfterInit, queryCount);
        assertEquals("+00:00", UserContext.init((UserContext) null).getTimezone());

        TenantContext.init("tenant-b");
        assertEquals("+05:45", UserContext.init((UserContext) null).getTimezone());
        assertEquals("-05:00", original.getTimezone());
    }

    /** 用户上下文统一规范化显式值，无效值回退当前租户默认值。 */
    @Test
    public void userInitializationNormalizesAndFallsBack() {
        configByTenant.put("tenant-a", "-05:00");
        UserVo user = new UserVo();
        user.setUserId("test-user");
        user.setAuthorization("test-token");
        assertEquals("-05:00", UserContext.init(user, null).getTimezone());
        assertEquals("-05:00", UserContext.init(user, null, "invalid").getTimezone());
        assertEquals("+08:00", UserContext.init(user, null, "+08:00").getTimezone());
        assertEquals("+00:00", UserContext.init(user, null, "Z").getTimezone());
    }

    /** 系统用户仅提供身份，新上下文使用租户默认值并规范化零偏移。 */
    @Test
    public void systemUserInitializationUsesTenantDefault() {
        UserVo user = new UserVo();
        user.setUserId("system-test");
        user.setAuthorization("test-token");
        ISystemUser systemUser = (ISystemUser) Proxy.newProxyInstance(ISystemUser.class.getClassLoader(),
                new Class<?>[]{ISystemUser.class}, (proxy, method, args) ->
                        "getUserVo".equals(method.getName()) ? user : null);
        configByTenant.put("tenant-a", "-05:00");
        assertEquals("-05:00", UserContext.init(systemUser).getTimezone());
        configByTenant.put("tenant-a", "Z");
        assertEquals("+00:00", UserContext.init(systemUser).getTimezone());
        assertEquals(2, queryCount);
    }

    /** 请求内切换身份时显式继承原时区，不能被租户默认值覆盖。 */
    @Test
    public void identitySwitchPreservesRequestTimezone() {
        UserVo user = new UserVo();
        user.setUserId("request-user");
        user.setAuthorization("test-token");
        configByTenant.put("tenant-a", "-05:00");
        UserContext.init(user, null, "+05:45");
        user.setUserId("execution-user");
        UserContext.init(user, null, UserContext.get().getTimezone());
        assertEquals("execution-user", UserContext.get().getUserId());
        assertEquals("+05:45", UserContext.get().getTimezone());
        assertEquals(0, queryCount);
    }

    /** 主认证链传入已解析时区后不再读取 Cookie。 */
    @Test
    public void authenticationUsesProvidedTimezoneWithoutReadingCookies() throws Exception {
        LoginAuthHandlerBase handler = new LoginAuthHandlerBase() {
            @Override
            public UserVo myAuth(HttpServletRequest request) {
                return null;
            }

            @Override
            public String getType() {
                return "test";
            }
        };
        CountingRequest mainRequest = new CountingRequest("+05:30");
        handler.auth(mainRequest.request, null, "+05:30");
        assertEquals(0, mainRequest.cookieReads);
    }

    /** 数据库连接只读取用户上下文中的时区，不重新查询租户参数。 */
    @Test
    public void databaseConnectionUsesResolvedContextWithoutConfigRecursion() throws Exception {
        configByTenant.put("tenant-a", "+05:45");
        UserContext.init((UserContext) null);
        int queriesAfterInit = queryCount;
        List<String> statements = new ArrayList<>();
        Statement statement = (Statement) Proxy.newProxyInstance(Statement.class.getClassLoader(),
                new Class<?>[]{Statement.class}, (proxy, method, args) -> {
                    if ("execute".equals(method.getName())) {
                        statements.add((String) args[0]);
                    }
                    return jdbcDefault(method);
                });
        Connection connection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class}, (proxy, method, args) -> {
                    if ("createStatement".equals(method.getName())) {
                        return statement;
                    }
                    if ("getTransactionIsolation".equals(method.getName())) {
                        return Connection.TRANSACTION_READ_COMMITTED;
                    }
                    if ("getAutoCommit".equals(method.getName()) || "isValid".equals(method.getName())) {
                        return true;
                    }
                    return jdbcDefault(method);
                });
        DataSource source = (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(),
                new Class<?>[]{DataSource.class}, (proxy, method, args) ->
                        "getConnection".equals(method.getName()) ? connection : jdbcDefault(method));
        try (NeatLogicBasicDataSource pool = new NeatLogicBasicDataSource()) {
            pool.setDataSource(source);
            pool.setMaximumPoolSize(1);
            pool.setMinimumIdle(0);
            try (Connection ignored = pool.getConnection()) {
                assertTrue(statements.contains("SET time_zone = '+05:45'"));
                assertEquals(queriesAfterInit, queryCount);
            }
        }
    }

    /** 为 JDBC 替身提供无副作用的基础返回值。 */
    private static Object jdbcDefault(Method method) {
        if (method.getReturnType() == boolean.class) {
            return false;
        }
        if (method.getReturnType() == int.class) {
            return 0;
        }
        if (method.getReturnType() == long.class) {
            return 0L;
        }
        return null;
    }

    /** 记录单个模拟请求读取 Cookie 的次数。 */
    private static final class CountingRequest {
        private int cookieReads;
        private final HttpServletRequest request;

        /** 每个实例表示一个独立请求，不共享 Cookie 读取计数。 */
        private CountingRequest(String value) {
            Cookie[] cookies = value == null ? null : new Cookie[]{new Cookie("neatlogic_timezone", value)};
            request = (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
                    new Class<?>[]{HttpServletRequest.class}, (proxy, method, args) -> {
                        if ("getCookies".equals(method.getName())) {
                            cookieReads++;
                            return cookies;
                        }
                        if ("getHeader".equals(method.getName())) {
                            return null;
                        }
                        return null;
                    });
        }
    }
}

package neatlogic.framework.store.mysql;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.dto.UserVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.MDC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 使用真实 NeatLogicBasicDataSource 和 MySQL TIMESTAMP(3) 验证跨用户读写时保留原始时间点。
 * <p>
 * 在 IDE 中作为 JUnit 4 测试运行，配置以下环境变量：
 * TIMEZONE_MYSQL_TEST_URL=jdbc:mysql://127.0.0.1:3306/测试库
 * TIMEZONE_MYSQL_TEST_USER=测试账号
 * TIMEZONE_MYSQL_TEST_PASSWORD=测试密码（允许空密码）
 * <p>
 * 账号需要临时表权限；仅创建连接级临时表，连接池关闭后自动删除，不写入工单表。
 * JDBC 初始时区为 +08:00，池中只保留一个物理连接，有用户时区时同步，缺失时沿用连接的时区。
 */
public class NeatLogicBasicDataSourceTimezoneIT {
    private static final String DRIVER_TIMEZONE = "+08:00";
    private static final String[] USER_TIMEZONES = {"+08:00", "+09:00", "+00:00", "-05:00", "+05:30", "+05:45"};
    /** 临时表只对当前物理连接可见，使用固定语句以覆盖预编译缓存复用。 */
    interface TimeMapper {
        /** 模拟业务 Mapper 直接绑定 Date 参数，同时由数据库生成两个对照时间。 */
        @Insert("INSERT INTO timezone_it (writer_zone, original_ms, java_time, now_time, now3_time) "
                + "VALUES (#{zone}, #{original}, #{date}, NOW(), NOW(3))")
        int insert(@Param("zone") String zone, @Param("original") long original, @Param("date") Date date);

        /** 将查询列映射成 java.util.Date，以相同的真实 Mapper 验证跨用户读取。 */
        @Select("SELECT java_time FROM timezone_it WHERE writer_zone = #{zone}")
        Date read(String zone);
    }

    /** 客户端预编译：在同一物理连接上切换用户，验证 SQL 文本中的时间绑定。 */
    @Test
    public void clientPreparedStatementsPreserveTime() throws Exception {
        verifyAcrossTimezones(false);
    }

    /** 服务端预编译：覆盖生产语句缓存及显式关闭 preserveInstants 的初始配置。 */
    @Test
    public void serverPreparedStatementsPreserveTime() throws Exception {
        verifyAcrossTimezones(true);
    }

    /** 对真实数据源同时验证写入、跨时区读取、无上下文借用和会话设置失败后的复用。 */
    private void verifyAcrossTimezones(boolean serverPrepare) throws Exception {
        String url = System.getenv("TIMEZONE_MYSQL_TEST_URL");
        Assume.assumeTrue("未设置 TIMEZONE_MYSQL_TEST_URL，跳过真实 MySQL 集成测试",
                url != null && !url.isBlank());
        String user = System.getenv("TIMEZONE_MYSQL_TEST_USER");
        assertTrue("请设置 TIMEZONE_MYSQL_TEST_USER", user != null && !user.isBlank());
        // 使用独立连接池，不读取应用配置；禁止 URL 参数覆盖本测试确定性的驱动时区配置。
        String lowerUrl = url.toLowerCase(Locale.ROOT);
        assertTrue("测试 URL 必须使用 jdbc:mysql://", lowerUrl.startsWith("jdbc:mysql://"));
        for (String option : new String[]{"connectiontimezone", "servertimezone", "preserveinstants",
                "forceconnectiontimezonetosession", "sessionvariables", "useserverprepstmts", "cacheprepstmts",
                "rewritebatchedstatements"}) {
            assertTrue("测试 URL 请移除参数：" + option, !lowerUrl.contains(option + "="));
        }

        UserContext previousUser = UserContext.get();
        String previousMdcUser = MDC.get("userId");
        try (NeatLogicBasicDataSource source = new NeatLogicBasicDataSource()) {
            source.setJdbcUrl(url);
            source.setUsername(user);
            String password = System.getenv("TIMEZONE_MYSQL_TEST_PASSWORD");
            source.setPassword(password == null ? "" : password);
            source.addDataSourceProperty("connectionTimeZone", DRIVER_TIMEZONE);
            source.addDataSourceProperty("preserveInstants", !serverPrepare);
            source.addDataSourceProperty("useServerPrepStmts", serverPrepare);
            source.addDataSourceProperty("cachePrepStmts", true);
            source.addDataSourceProperty("rewriteBatchedStatements", true);
            source.addDataSourceProperty("forceConnectionTimeZoneToSession", "false");
            source.setMaximumPoolSize(1);
            source.setMinimumIdle(1);
            source.setMaxLifetime(0);
            source.setConnectionTimeout(10000);
            source.setPoolName("timezone-integration-test-" + serverPrepare);
            Configuration configuration = new Configuration(new Environment("timezone-it",
                    new JdbcTransactionFactory(), source));
            configuration.addMapper(TimeMapper.class);
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);

            switchUser(DRIVER_TIMEZONE);
            long connectionId;
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
                connectionId = connectionId(connection);
                statement.execute("CREATE TEMPORARY TABLE timezone_it ("
                        + "writer_zone VARCHAR(6) PRIMARY KEY, original_ms BIGINT NOT NULL, "
                        + "java_time TIMESTAMP(3) NOT NULL, now_time TIMESTAMP(3) NOT NULL, "
                        + "now3_time TIMESTAMP(3) NOT NULL) "
                        + "DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");
                System.out.printf("JVM 时区=%s，JDBC 初始时区=%s，驱动=%s，数据库=%s%n",
                        ZoneOffset.systemDefault(), DRIVER_TIMEZONE,
                        connection.getMetaData().getDriverVersion(), connection.getMetaData().getDatabaseProductVersion());
            }

            System.out.println("写入用户 | 写入方式 | Java 原始毫秒 | 数据库存储毫秒 | 相对 Java 时间偏移(ms)");
            for (String writerZone : USER_TIMEZONES) {
                switchUser(writerZone);
                try (SqlSession session = factory.openSession(true)) {
                    assertSession(session.getConnection(), connectionId, writerZone);
                    insertAndCheck(session, writerZone);
                }
            }

            System.out.println("读取用户 | 写入用户 | 写入方式 | 数据库存储毫秒 | JDBC 读回毫秒 | 读取偏移(ms)");
            for (String readerZone : USER_TIMEZONES) {
                switchUser(readerZone);
                try (SqlSession session = factory.openSession(true)) {
                    assertSession(session.getConnection(), connectionId, readerZone);
                    readAndCheck(session, readerZone);
                }
            }
            verifyJdbcBatchAndBackgroundRead(source, connectionId);
            // MySQL 拒绝 +16:00 时沿用原有异常处理，服务端和驱动仍应保留上一次的 +08:00。
            switchUser("+16:00");
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
                assertSession(connection, connectionId, "+08:00");
                try (ResultSet rs = statement.executeQuery("SELECT ts, UNIX_TIMESTAMP(ts) * 1000 AS ms FROM timezone_batch WHERE id = 1")) {
                    assertTrue(rs.next());
                    assertEquals(rs.getLong("ms"), rs.getTimestamp("ts").getTime());
                }
            }
        } finally {
            if (UserContext.get() != null) {
                UserContext.get().release();
            }
            if (previousUser != null) {
                UserContext.init(previousUser);
            }
            if (previousMdcUser == null) {
                MDC.remove("userId");
            } else {
                MDC.put("userId", previousMdcUser);
            }
        }
    }

    /** 初始化不同用户的真实上下文；显式授权值避免触发登录令牌生成。 */
    private static void switchUser(String timezone) {
        UserVo user = new UserVo();
        user.setUserId("timezone-test-" + timezone);
        user.setAuthorization("timezone-test-token");
        UserContext.init(user, null, timezone);
    }

    /** 确认复用同一物理连接，并检查实际会话时区。 */
    private static void assertSession(Connection connection, long expectedId, String timezone) throws Exception {
        assertEquals("连接池应复用同一物理连接", expectedId, connectionId(connection));
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT @@session.time_zone")) {
            assertTrue(result.next());
            assertEquals("MySQL 会话时区", timezone, result.getString(1));
        }
    }

    /** 读取服务端连接 ID，避免临时表测试意外跨物理连接。 */
    private static long connectionId(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT CONNECTION_ID()")) {
            assertTrue(result.next());
            return result.getLong(1);
        }
    }

    /** 使用服务器时间界定 NOW 的真实时间范围，避免 JVM 与数据库主机时钟差影响断言。 */
    private static long serverMillis(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT UNIX_TIMESTAMP(NOW(3)) * 1000")) {
            assertTrue(result.next());
            return result.getBigDecimal(1).longValueExact();
        }
    }

    /** 通过 Mapper 绑定 new Date，与服务端 NOW、NOW(3) 同行写入。 */
    private static void insertAndCheck(SqlSession session, String writerZone) throws Exception {
        Connection connection = session.getConnection();
        long before = serverMillis(connection);
        Date date = new Date();
        assertEquals(1, session.getMapper(TimeMapper.class).insert(writerZone, date.getTime(), date));
        long after = serverMillis(connection);
        try (PreparedStatement statement = connection.prepareStatement("SELECT "
                + "UNIX_TIMESTAMP(java_time) * 1000 AS java_ms, "
                + "UNIX_TIMESTAMP(now_time) * 1000 AS now_ms, "
                + "UNIX_TIMESTAMP(now3_time) * 1000 AS now3_ms FROM timezone_it WHERE writer_zone = ?")) {
            statement.setString(1, writerZone);
            try (ResultSet result = statement.executeQuery()) {
                assertTrue(result.next());
                long javaMs = result.getBigDecimal("java_ms").longValueExact();
                long nowMs = result.getBigDecimal("now_ms").longValueExact();
                long now3Ms = result.getBigDecimal("now3_ms").longValueExact();
                printWrite(writerZone, "new Date()", date.getTime(), javaMs);
                printWrite(writerZone, "NOW()", date.getTime(), nowMs);
                printWrite(writerZone, "NOW(3)", date.getTime(), now3Ms);
                assertEquals("数据库必须保存 Java 原始毫秒，用户=" + writerZone, date.getTime(), javaMs);
                assertTrue("NOW() 应位于服务器操作时间范围内（秒精度）",
                        nowMs >= before / 1000 * 1000 && nowMs <= after);
                assertTrue("NOW(3) 应位于服务器操作时间范围内", now3Ms >= before && now3Ms <= after);
                assertEquals("同一语句 NOW() 与 NOW(3) 只应相差毫秒部分", now3Ms / 1000 * 1000, nowMs);
            }
        }
    }

    /** 对每位读取用户分别比较 SQL 的原始毫秒、JDBC 读值和真实 Mapper 返回值。 */
    private static void readAndCheck(SqlSession session, String readerZone) throws Exception {
        TimeMapper mapper = session.getMapper(TimeMapper.class);
        try (Statement statement = session.getConnection().createStatement();
             ResultSet result = statement.executeQuery("SELECT *, "
                     + "UNIX_TIMESTAMP(java_time) * 1000 AS java_ms, "
                     + "UNIX_TIMESTAMP(now_time) * 1000 AS now_ms, "
                     + "UNIX_TIMESTAMP(now3_time) * 1000 AS now3_ms FROM timezone_it ORDER BY writer_zone")) {
            int count = 0;
            while (result.next()) {
                count++;
                String writerZone = result.getString("writer_zone");
                long original = result.getLong("original_ms");
                for (String prefix : new String[]{"java", "now", "now3"}) {
                    long stored = result.getBigDecimal(prefix + "_ms").longValueExact();
                    long read = result.getTimestamp(prefix + "_time").getTime();
                    System.out.printf("%s | %s | %s | %d | %d | %+d%n",
                            readerZone, writerZone, prefix, stored, read, read - stored);
                    assertEquals("JDBC 读取偏移，读取用户=" + readerZone + "，写入用户=" + writerZone,
                            stored, read);
                    if ("java".equals(prefix)) {
                        assertEquals("落库毫秒应等于原始毫秒", original, stored);
                        assertEquals("Mapper 读回毫秒应等于原始毫秒", original, mapper.read(writerZone).getTime());
                    }
                }
            }
            assertEquals("应读取全部用户写入的数据", USER_TIMEZONES.length, count);
        }
    }

    /** 输出存储差值；NOW 相对 Java 的小幅差值可能包含主机时钟差和执行耗时。 */
    private static void printWrite(String timezone, String method, long original, long stored) {
        System.out.printf("%s | %s | %d | %d | %+d%n", timezone, method, original, stored, stored - original);
    }

    /** 覆盖 JDBC 批量绑定、空值及 DATETIME 按当地钟点和 Timestamp 两种读法。 */
    private static void verifyJdbcBatchAndBackgroundRead(NeatLogicBasicDataSource source, long id) throws Exception {
        switchUser("+09:00");
        LocalDateTime local = LocalDateTime.of(2026, 11, 5, 11, 0);
        Timestamp instant = Timestamp.from(local.toInstant(ZoneOffset.ofHours(9)));
        try (Connection connection = source.getConnection(); Statement ddl = connection.createStatement()) {
            assertSession(connection, id, "+09:00");
            ddl.execute("CREATE TEMPORARY TABLE timezone_batch (id INT PRIMARY KEY, ts TIMESTAMP(3) NULL, "
                    + "wall DATETIME(3)) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");
            try (PreparedStatement insert = connection.prepareStatement("INSERT INTO timezone_batch VALUES (?, ?, ?)")) {
                insert.setInt(1, 1);
                insert.setTimestamp(2, instant);
                insert.setObject(3, local);
                insert.addBatch();
                insert.setInt(1, 2);
                insert.setTimestamp(2, null);
                insert.setObject(3, null);
                insert.addBatch();
                assertEquals(2, insert.executeBatch().length);
            }
        }
        // 无用户上下文时沿用这条物理连接的时区；上海用户随后借用时才切换。
        UserContext.get().release();
        try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
            assertSession(connection, id, "+09:00");
            try (ResultSet rs = statement.executeQuery("SELECT ts FROM timezone_batch WHERE id = 1")) {
                assertTrue(rs.next());
                assertEquals(instant, rs.getTimestamp("ts"));
            }
        }
        switchUser("+08:00");
        try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
            assertSession(connection, id, "+08:00");
            try (ResultSet rs = statement.executeQuery("SELECT ts, wall, UNIX_TIMESTAMP(ts) * 1000 AS ms "
                    + "FROM timezone_batch ORDER BY id")) {
                assertTrue(rs.next());
                assertEquals(instant.getTime(), rs.getLong("ms"));
                assertEquals(instant, rs.getTimestamp("ts"));
                assertEquals(local, rs.getObject("wall", LocalDateTime.class));
                assertTrue(rs.getString("wall").startsWith("2026-11-05 11:00:00"));
                assertEquals(local.toInstant(ZoneOffset.ofHours(8)), rs.getTimestamp("wall").toInstant());
                assertTrue(rs.next());
                assertNull(rs.getTimestamp("ts"));
                assertNull(rs.getObject("wall", LocalDateTime.class));
            }
        }
    }
}

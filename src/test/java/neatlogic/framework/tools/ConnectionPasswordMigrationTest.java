package neatlogic.framework.tools;

import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.crypto.handler.RSACryptoHandler;
import neatlogic.framework.dto.DatasourceVo;
import neatlogic.framework.dto.ElasticsearchVo;
import neatlogic.framework.dto.MongoDbVo;
import neatlogic.framework.util.ConnectionPasswordUtil;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;

/** 仅在显式配置测试连接时创建专用临时库，绝不操作业务库。 */
public class ConnectionPasswordMigrationTest {
    private static final List<String> TABLES = List.of("mongodb", "elasticsearch", "datasource");
    private Connection admin;
    private Connection connection;
    private String database;
    private String url;
    private String user;
    private String password;

    /** 在无业务数据的独立库中重建旧字段结构。 */
    @Before
    public void setUp() throws Exception {
        url = System.getenv("NEATLOGIC_PASSWORD_TEST_URL");
        user = System.getenv("NEATLOGIC_PASSWORD_TEST_USER");
        password = System.getenv("NEATLOGIC_PASSWORD_TEST_PASSWORD");
        assumeNotNull(url, user, password);
        admin = DriverManager.getConnection(url, user, password);
        database = "nl_pwd_test_" + UUID.randomUUID().toString().replace("-", "");
        execute(admin, "CREATE DATABASE `" + database + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
        connection = DriverManager.getConnection(url, user, password);
        connection.setCatalog(database);
        for (String table : TABLES) {
            execute(connection, "CREATE TABLE `" + table + "` (tenant_uuid VARCHAR(50) PRIMARY KEY, "
                    + "password VARCHAR(50) NULL, username VARCHAR(255), host VARCHAR(1025), port INT, "
                    + "url VARCHAR(1000), driver VARCHAR(255), `database` VARCHAR(50), "
                    + "`option` TEXT, auth_config TEXT, config TEXT) ENGINE=InnoDB");
        }
    }

    /** 只删除本测试创建的随机库。 */
    @After
    public void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
        if (admin != null) {
            try {
                if (database != null) {
                    execute(admin, "DROP DATABASE `" + database + "`");
                }
            } finally {
                admin.close();
            }
        }
    }

    /** 真实扩容SQL、预览不写入、迁移后可认证以及再次执行幂等。 */
    @Test
    public void schemaPreviewApplyAndRepeat() throws Exception {
        expandSchema();
        String rc4 = RC4Util.encrypt("legacy");
        String rsa = new RSACryptoHandler().encrypt("legacy");
        String hybrid = ConnectionPasswordUtil.encryptPlain("legacy");
        String brace = "{AES}" + hybrid.substring("AES:".length());
        for (String table : TABLES) {
            insert(table, "plain", "secret");
            insert(table, "rc4", rc4);
            insert(table, "old", rc4.replace("{RC4}", "RC4:"));
            insert(table, "tagent", rc4.replace("{RC4}", "{ENCRYPTED}"));
            insert(table, "rsa", rsa);
            insert(table, "hybrid", hybrid);
            insert(table, "brace", brace);
            insert(table, "empty", "");
            insert(table, "null", null);
        }
        String preview = migrate(false);
        assertEquals(3, occurrences(preview, "待转换=1，已转换=0，跳过=8"));
        assertEquals("secret", read("mongodb", "plain"));
        String result = migrate(true);
        assertEquals(3, occurrences(result, "待转换=1，已转换=1，跳过=8"));
        for (String table : TABLES) {
            assertTrue(read(table, "plain").startsWith("AES:"));
            assertEquals("secret", ConnectionPasswordUtil.decrypt(read(table, "plain")));
            assertEquals(rc4, read(table, "rc4"));
            assertEquals(rsa, read(table, "rsa"));
            assertEquals(hybrid, read(table, "hybrid"));
            assertEquals(brace, read(table, "brace"));
        }
        String stable = read("mongodb", "plain");
        assertEquals(3, occurrences(migrate(true), "待转换=0，已转换=0，跳过=9"));
        assertEquals(stable, read("mongodb", "plain"));
        assertFalse(result.contains("secret"));
        assertFalse(result.contains(stable));
    }

    /** 字段未扩容时，预览及执行都必须拒绝。 */
    @Test
    public void rejectsNarrowSchema() throws Exception {
        insert("mongodb", "plain", "secret");
        for (boolean apply : List.of(false, true)) {
            try {
                migrate(apply);
                fail("未扩容不应接受迁移");
            } catch (SQLException expected) {
                assertEquals("secret", read("mongodb", "plain"));
            }
        }
    }

    /** 最后一张表写入失败时，前两张表已经执行的更新也必须回滚。 */
    @Test
    public void failureRollsBackAllTables() throws Exception {
        expandSchema();
        for (String table : TABLES) {
            insert(table, "plain", "secret");
        }
        execute(connection, "CREATE TRIGGER reject_migration BEFORE UPDATE ON datasource FOR EACH ROW "
                + "SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='测试回滚'");
        try {
            migrate(true);
            fail("触发器拒绝更新后应回滚");
        } catch (SQLException expected) {
            for (String table : TABLES) {
                assertEquals("secret", read(table, "plain"));
            }
        }
    }

    /** 模拟快照之后发生仅大小写不同的并发修改，验证精确比较及跨表回滚。 */
    @Test
    public void concurrentChangeIsNotOverwritten() throws Exception {
        expandSchema();
        for (String table : TABLES) {
            insert(table, "plain", "secret");
        }
        Connection concurrent = (Connection) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{Connection.class}, (proxy, method, args) -> {
                    if ("prepareStatement".equals(method.getName()) && args[0].toString().startsWith("UPDATE `datasource`")) {
                        try (Connection other = DriverManager.getConnection(url, user, password)) {
                            other.setCatalog(database);
                            execute(other, "UPDATE datasource SET password='SECRET' WHERE tenant_uuid='plain'");
                        }
                    }
                    try {
                        return method.invoke(connection, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
        try {
            ConnectionPasswordMigrationTool.migrate(concurrent, true, new PrintStream(new ByteArrayOutputStream()));
            fail("并发更新不应被覆盖");
        } catch (SQLException expected) {
            assertEquals("secret", read("mongodb", "plain"));
            assertEquals("secret", read("elasticsearch", "plain"));
            assertEquals("SECRET", read("datasource", "plain"));
        }
    }

    /** 执行真实Mapper的新增、重复键更新和回读，覆盖认证明文的来源。 */
    @Test
    public void realMappersInsertUpdateAndReadCipher() throws Exception {
        expandSchema();
        // Elasticsearch真实结构没有option列，避免测试表掩盖字段映射错误。
        execute(connection, "ALTER TABLE elasticsearch DROP COLUMN `option`");
        Configuration configuration = new Configuration();
        configuration.setEnvironment(new Environment("password-test", new JdbcTransactionFactory(), new UnpooledDataSource()));
        String[] names = {"Datasource", "MongoDb", "Elasticsearch"};
        String[] inserts = {"insertDatasource", "insertTenantMongodb", "insertTenantElasticsearch"};
        String[] selects = {"getDatasourceByTenantUuid", "getTenantMongoDbByTenantUuid", "getTenantElasticsearchByTenantUuid"};
        Object[] vos = {new DatasourceVo(), new MongoDbVo(), new ElasticsearchVo()};
        for (String name : names) {
            String path = "neatlogic/framework/dao/mapper/" + name + "Mapper.xml";
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
                new XMLMapperBuilder(stream, configuration, path, configuration.getSqlFragments()).parse();
            }
        }
        try (SqlSession session = new SqlSessionFactoryBuilder().build(configuration).openSession(connection)) {
            for (int i = 0; i < names.length; i++) {
                Object vo = vos[i];
                configuration.newMetaObject(vo).setValue("tenantUuid", "mapper-test");
                configuration.newMetaObject(vo).setValue("host", "127.0.0.1");
                if (vo instanceof DatasourceVo) {
                    ((DatasourceVo) vo).setPort(3306);
                }
                if (vo instanceof ElasticsearchVo) {
                    ((ElasticsearchVo) vo).setConfigStr("{\"scheme\":\"https\"}");
                }
                String namespace = "neatlogic.framework.dao.mapper." + names[i] + "Mapper.";
                for (String plain : List.of("insert-password", "updated-password")) {
                    configuration.newMetaObject(vo).setValue("passwordPlain", plain);
                    session.insert(namespace + inserts[i], vo);
                    Object loaded = session.selectOne(namespace + selects[i], "mapper-test");
                    String stored = (String) configuration.newMetaObject(loaded).getValue("passwordCipher");
                    assertTrue(stored.startsWith("AES:"));
                    assertEquals(plain, configuration.newMetaObject(loaded).getValue("passwordPlain"));
                    if (loaded instanceof ElasticsearchVo) {
                        assertEquals("https", ((ElasticsearchVo) loaded).getConfig().getString("scheme"));
                    }
                }
            }
        }
    }

    /** 独立Java进程验证命令行默认预览和显式执行开关，密码只通过环境传入。 */
    @Test
    public void commandLineDefaultsToPreview() throws Exception {
        expandSchema();
        for (String table : TABLES) {
            insert(table, "plain", "cli-password");
        }
        runCommand(false);
        assertEquals("cli-password", read("mongodb", "plain"));
        runCommand(true);
        assertEquals("cli-password", ConnectionPasswordUtil.decrypt(read("mongodb", "plain")));
        assertTrue(read("mongodb", "plain").startsWith("AES:"));
    }

    /** 使用本次编译产物启动CLI，并检查退出码和摘要，避免只验证内部方法。 */
    private void runCommand(boolean apply) throws Exception {
        URI uri = URI.create(url.substring(5));
        String databaseUrl = "jdbc:mysql://" + uri.getRawAuthority() + "/" + database;
        if (uri.getRawQuery() != null) {
            databaseUrl += "?" + uri.getRawQuery();
        }
        List<String> command = new ArrayList<>(List.of(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", System.getProperty("surefire.test.class.path", System.getProperty("java.class.path")),
                ConnectionPasswordMigrationTool.class.getName(), "--url", databaseUrl, "--user", user));
        if (apply) {
            command.add("--apply");
        }
        ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true);
        builder.environment().put("NEATLOGIC_MIGRATION_PASSWORD", password);
        Process process = builder.start();
        String result = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(result, 0, process.waitFor());
        assertTrue(result.contains("待转换=1"));
        assertFalse(result.contains("cli-password"));
    }

    /** 执行随版本交付的真实扩容脚本。 */
    private void expandSchema() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(
                "neatlogic/resources/framework/changelog/2026-11-03/neatlogic.sql")) {
            assertNotNull(stream);
            for (String sql : new String(stream.readAllBytes(), StandardCharsets.UTF_8).split(";")) {
                if (!sql.trim().isEmpty()) {
                    execute(connection, sql);
                }
            }
        }
    }

    /** 捕获工具输出，验证统计及敏感信息边界。 */
    private String migrate(boolean apply) throws SQLException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConnectionPasswordMigrationTool.migrate(connection, apply, new PrintStream(output));
        return output.toString(StandardCharsets.UTF_8);
    }

    /** 写入测试记录，密码始终通过参数绑定。 */
    private void insert(String table, String id, String value) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + table + "` (tenant_uuid,password) VALUES (?,?)")) {
            statement.setString(1, id);
            statement.setString(2, value);
            statement.executeUpdate();
        }
    }

    /** 读取隔离库中的单条记录。 */
    private String read(String table, String id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT password FROM `" + table + "` WHERE tenant_uuid=?")) {
            statement.setString(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                assertTrue(rows.next());
                return rows.getString(1);
            }
        }
    }

    /** 执行测试结构语句。 */
    private static void execute(Connection target, String sql) throws SQLException {
        try (Statement statement = target.createStatement()) {
            statement.execute(sql);
        }
    }

    /** 统计每张表的摘要，避免依赖表遍历顺序。 */
    private static int occurrences(String text, String part) {
        return text.split(java.util.regex.Pattern.quote(part), -1).length - 1;
    }
}

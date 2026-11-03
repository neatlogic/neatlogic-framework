package neatlogic.framework.tools;

import neatlogic.framework.util.ConnectionPasswordUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Console;
import java.io.PrintStream;
import java.sql.*;
import java.util.*;

/** 由运维手动执行的基础连接密码迁移工具，不参与应用启动。 */
public final class ConnectionPasswordMigrationTool {
    private static final List<String> TABLES = List.of("mongodb", "elasticsearch", "datasource");

    private ConnectionPasswordMigrationTool() {
    }

    /** 默认仅预览；数据库密码从环境变量或终端读取，错误输出不包含底层连接信息。 */
    public static void main(String[] args) {
        try {
            Map<String, String> options = parseOptions(args);
            String password = System.getenv("NEATLOGIC_MIGRATION_PASSWORD");
            if (password == null) {
                Console console = System.console();
                if (console == null) {
                    throw new IllegalArgumentException("请设置NEATLOGIC_MIGRATION_PASSWORD或使用交互终端");
                }
                char[] chars = console.readPassword("数据库连接密码：");
                if (chars == null) {
                    throw new IllegalArgumentException("未输入数据库连接密码");
                }
                password = new String(chars);
                Arrays.fill(chars, '\0');
            }
            try (Connection connection = DriverManager.getConnection(options.get("--url"), options.get("--user"), password)) {
                migrate(connection, options.containsKey("--apply"), System.out);
            }
        } catch (Exception e) {
            System.err.println("密码迁移失败，未提交更新。请检查参数、字段容量、事务支持及并发修改；不输出底层异常以保护密码。");
            System.exit(1);
        }
    }

    /** 只接受独立连接参数及显式执行开关，拒绝在URL中携带认证信息。 */
    private static Map<String, String> parseOptions(String[] args) {
        Map<String, String> options = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if (options.containsKey(option)) {
                throw new IllegalArgumentException("参数重复");
            }
            if ("--apply".equals(option)) {
                options.put(option, "true");
            } else if (("--url".equals(option) || "--user".equals(option)) && i + 1 < args.length) {
                options.put(option, args[++i]);
            } else {
                throw new IllegalArgumentException("用法：--url <JDBC地址> --user <账号> [--apply]");
            }
        }
        String url = options.get("--url");
        if (StringUtils.isBlank(url) || StringUtils.isBlank(options.get("--user"))
                || !url.startsWith("jdbc:mysql://") || url.contains("@")
                || url.toLowerCase(Locale.ROOT).matches(".*[?&](user|password)=.*")) {
            throw new IllegalArgumentException("需要不含认证信息的MySQL JDBC地址及账号");
        }
        return options;
    }

    /** 校验结构后预览或原子迁移三张表，任何失败均回滚，不改动实际数据库账号。 */
    public static void migrate(Connection connection, boolean apply, PrintStream output) throws SQLException {
        if (!connection.getAutoCommit()) {
            throw new SQLException("迁移需要独立的自动提交连接");
        }
        checkSchema(connection);
        connection.setAutoCommit(false);
        try {
            List<Change> changes = new ArrayList<>();
            Map<String, Integer> skipped = new LinkedHashMap<>();
            for (String table : TABLES) {
                int skipCount = 0;
                // 表名来自固定白名单；密码只保存在内存及预编译参数中，不写日志。
                try (Statement statement = connection.createStatement();
                     ResultSet rows = statement.executeQuery("SELECT tenant_uuid, password FROM `" + table + "`")) {
                    while (rows.next()) {
                        String original = rows.getString("password");
                        if (StringUtils.isBlank(original) || ConnectionPasswordUtil.isEncrypted(original)) {
                            skipCount++;
                            continue;
                        }
                        String cipher = ConnectionPasswordUtil.encryptPlain(original);
                        if (cipher.length() > 65535) {
                            throw new SQLException("密文超出TEXT容量");
                        }
                        changes.add(new Change(table, rows.getString("tenant_uuid"), original, cipher));
                    }
                }
                skipped.put(table, skipCount);
            }
            if (apply) {
                for (Change change : changes) {
                    update(connection, change);
                }
                connection.commit();
            } else {
                connection.rollback();
            }
            output.println("预览或执行完成；已有密文仅跳过，不代表已验证其有效性。");
            for (String table : TABLES) {
                long count = changes.stream().filter(change -> table.equals(change.table)).count();
                long updated = 0;
                if (apply) {
                    updated = count;
                }
                output.printf("%s：待转换=%d，已转换=%d，跳过=%d%n", table, count, updated, skipped.get(table));
            }
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /** 仅对已扩容且支持事务的表执行，防止截断密文或出现部分提交。 */
    private static void checkSchema(Connection connection) throws SQLException {
        String sql = "SELECT c.DATA_TYPE, c.CHARACTER_OCTET_LENGTH, t.ENGINE "
                + "FROM information_schema.COLUMNS c JOIN information_schema.TABLES t "
                + "ON c.TABLE_SCHEMA=t.TABLE_SCHEMA AND c.TABLE_NAME=t.TABLE_NAME "
                + "WHERE c.TABLE_SCHEMA=DATABASE() AND c.TABLE_NAME=? AND c.COLUMN_NAME='password'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String table : TABLES) {
                statement.setString(1, table);
                try (ResultSet rows = statement.executeQuery()) {
                    if (!rows.next()
                            || !List.of("text", "mediumtext", "longtext").contains(rows.getString(1).toLowerCase(Locale.ROOT))
                            || rows.getLong(2) < 65535 || !"InnoDB".equalsIgnoreCase(rows.getString(3))) {
                        throw new SQLException("请先扩容密码字段并确认三张表使用InnoDB事务引擎");
                    }
                }
            }
        }
    }

    /** 使用字节精确比较原值，避免不区分大小写的排序规则掩盖并发密码修改。 */
    private static void update(Connection connection, Change change) throws SQLException {
        String sql = "UPDATE `" + change.table + "` SET password=? WHERE tenant_uuid=? "
                + "AND CAST(password AS BINARY)=CAST(? AS BINARY)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, change.cipher);
            statement.setString(2, change.tenantUuid);
            statement.setString(3, change.original);
            if (statement.executeUpdate() != 1 || statement.getWarnings() != null) {
                throw new SQLException("密码记录发生并发修改或写入警告，本次迁移回滚");
            }
        }
    }

    /** 单次迁移的内存快照，禁止提供包含密码的字符串表示。 */
    private static final class Change {
        private final String table;
        private final String tenantUuid;
        private final String original;
        private final String cipher;

        /** 记录待转换值，供精确更新使用。 */
        private Change(String table, String tenantUuid, String original, String cipher) {
            this.table = table;
            this.tenantUuid = tenantUuid;
            this.original = original;
            this.cipher = cipher;
        }
    }
}

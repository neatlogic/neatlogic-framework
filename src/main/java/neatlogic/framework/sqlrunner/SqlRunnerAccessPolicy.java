package neatlogic.framework.sqlrunner;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auth.core.AuthActionChecker;
import neatlogic.framework.auth.label.ADMIN;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.sql.SqlRunnerAccessDeniedException;
import neatlogic.framework.store.mysql.DatasourceManager;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserConstants;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.Token;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/** 仅供动态 SQL 执行器使用的当前租户双库访问策略。 */
final class SqlRunnerAccessPolicy {
    private static final int MAX_DEPTH = 256;
    private static final int MAX_NODES = 20000;

    /** 工具类不允许实例化。 */
    private SqlRunnerAccessPolicy() {
    }

    /** 确认当前租户路由已注册，拒绝共享数据源向管理库回退。 */
    static void validateTenant() {
        TenantContext context = currentTenant();
        String key = DatasourceManager.getCurrentDatasourceKey();
        String expected = context.getTenantUuid();
        if (Boolean.TRUE.equals(context.isData())) {
            expected = DatasourceManager.getTenantDataDatasourceKey(expected);
        }
        if (!expected.equals(key) || DatasourceManager.getDatasource(key) == null) {
            throw new SqlRunnerAccessDeniedException();
        }
    }

    /** 以实际连接的当前库为准，阻止旧事务连接、自定义连接或管理库回退。 */
    static void validateConnection(Connection connection) {
        validateTenant();
        try {
            if (connection == null || !isAllowedDatabase(connection.getCatalog())) {
                throw new SqlRunnerAccessDeniedException();
            }
        } catch (SQLException ex) {
            // 驱动无法确认当前库时失败关闭，不向调用方传递连接错误详情。
            throw new SqlRunnerAccessDeniedException();
        }
    }

    /** 展开数据仓库占位符，并校验动态表达式求值后的单条查询 SQL。 */
    static String validateSql(String sql) {
        TenantContext context = currentTenant();
        if (StringUtils.isBlank(sql)) {
            throw new SqlRunnerAccessDeniedException();
        }
        String expanded = sql.replace("@{DATA_SCHEMA}", context.getDataDbName());
        String upperSql = expanded.toUpperCase(Locale.ROOT);
        // 数据库会执行的版本注释不能按普通注释交给解析器忽略。
        if (upperSql.contains("/*!") || upperSql.contains("/*M!") || upperSql.contains("/*T!")) {
            throw new SqlRunnerAccessDeniedException();
        }
        try {
            validateTokens(expanded);
            Statements statements = CCJSqlParserUtil.parseStatements(expanded);
            if (statements.getStatements().size() != 1
                    || !(statements.getStatements().get(0) instanceof Select)) {
                throw new SqlRunnerAccessDeniedException();
            }
            Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
            inspect(statements.getStatements().get(0), visited, 0);
            return expanded;
        } catch (JSQLParserException | ReflectiveOperationException | RuntimeException | StackOverflowError ex) {
            // 解析和遍历失败均拒绝执行；解析异常可能包含 SQL，禁止保留原始异常。
            throw new SqlRunnerAccessDeniedException();
        }
    }

    /**
     * 消除解析器与 MySQL 词法差异，防止注释、反斜杠或非标准引用隐藏真正执行的 SQL。
     * 使用词法单元区分注释和普通字符串，保留字符串中的 URL 及连续减号。
     */
    private static void validateTokens(String sql) {
        CCJSqlParser parser = CCJSqlParserUtil.newParser(sql);
        int count = 0;
        while (true) {
            Token token = parser.getNextToken();
            if (++count > MAX_NODES) {
                throw new SqlRunnerAccessDeniedException();
            }
            for (Token comment = token.specialToken; comment != null; comment = comment.specialToken) {
                String text = comment.image;
                // MySQL 不将双斜杠作为注释，双减号注释还必须紧跟空白或控制字符。
                if (text.startsWith("//") || (text.startsWith("--")
                        && (text.length() <= 2 || text.charAt(2) > 0x20))) {
                    throw new SqlRunnerAccessDeniedException();
                }
            }
            // 原样字面量不能依赖会话的 NO_BACKSLASH_ESCAPES 模式；参数绑定不受此限制。
            if (token.image.indexOf('\\') >= 0 || token.image.startsWith("$$")) {
                throw new SqlRunnerAccessDeniedException();
            }
            if (token.kind == CCJSqlParserConstants.EOF) {
                return;
            }
        }
    }

    /** 获取可信租户上下文；缺失上下文时不能使用默认管理库。 */
    private static TenantContext currentTenant() {
        TenantContext context = TenantContext.get();
        if (context == null || StringUtils.isBlank(context.getTenantUuid())) {
            throw new SqlRunnerAccessDeniedException();
        }
        return context;
    }

    /** 严格匹配物理库名，避免大小写敏感数据库中同名不同大小写的库越界。 */
    private static boolean isAllowedDatabase(String database) {
        TenantContext context = currentTenant();
        return context.getDbName().equals(database) || context.getDataDbName().equals(database);
    }

    /** 去掉标识符的合法反引号引用，不对物理库名做大小写折叠。 */
    private static String identifier(String name) {
        if (name != null && name.length() >= 2 && name.startsWith("`") && name.endsWith("`")) {
            return name.substring(1, name.length() - 1).replace("``", "`");
        }
        return name;
    }

    /** 校验所有表和列限定名，不将显式库名误识别为 CTE 或表别名。 */
    private static void inspectTable(Table table) {
        String schema = table.getSchemaName();
        String expectedName = table.getName();
        // 没有ADMIN权限不能查询黑名单表
        if (CollectionUtils.isNotEmpty(Config.REPORT_QUERY_TABLE_BLACKLIST()) && !AuthActionChecker.check(ADMIN.class)) {
            String tableName = expectedName;
            if (tableName.startsWith("`")) {
                tableName = tableName.substring(1);
            }
            if (tableName.endsWith("`")) {
                tableName = tableName.substring(0, tableName.length() - 1);
            }
            if (Config.REPORT_QUERY_TABLE_BLACKLIST().contains(tableName.toLowerCase(Locale.ROOT))) {
                throw new SqlRunnerAccessDeniedException();
            }
        }
        if (schema != null) {
            expectedName = schema + "." + expectedName;
        }
        // Table#getDatabase() 不保留四段名的服务器部分，必须核对完整限定名。
        if (!expectedName.equals(table.getFullyQualifiedName())) {
            throw new SqlRunnerAccessDeniedException();
        }
        if (schema != null && !isAllowedDatabase(identifier(schema))) {
            throw new SqlRunnerAccessDeniedException();
        }
    }

    /** 显式限定库的函数也遵守双库范围，禁止直接读取服务器文件的函数。 */
    private static void inspectFunction(Function function) {
        List<String> parts = function.getMultipartName();
        if (parts == null || parts.isEmpty() || parts.size() > 2) {
            throw new SqlRunnerAccessDeniedException();
        }
        if (parts.size() == 2 && !isAllowedDatabase(identifier(parts.get(0)))) {
            throw new SqlRunnerAccessDeniedException();
        }
        if ("LOAD_FILE".equalsIgnoreCase(identifier(parts.get(parts.size() - 1)))) {
            throw new SqlRunnerAccessDeniedException();
        }
    }

    /**
     * 遍历解析模型的全部字段，覆盖 JOIN ON、排序、分组和窗口表达式中的子查询。
     * 4.4 版本的 TablesNamesFinder 会遗漏这些位置，不能用其结果作为安全边界。
     * 排除底层 parser 节点图，并通过身份集合和深度限制避免环及过深模型。
     */
    private static void inspect(Object value, Set<Object> visited, int depth) throws ReflectiveOperationException {
        if (value == null || !visited.add(value)) {
            return;
        }
        if (depth > MAX_DEPTH || visited.size() > MAX_NODES) {
            throw new SqlRunnerAccessDeniedException();
        }
        if (value instanceof Iterable<?> items) {
            for (Object item : items) {
                inspect(item, visited, depth + 1);
            }
            return;
        }
        if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                inspect(entry.getKey(), visited, depth + 1);
                inspect(entry.getValue(), visited, depth + 1);
            }
            return;
        }
        Class<?> type = value.getClass();
        if (!type.getName().startsWith("net.sf.jsqlparser.") || type.isEnum()
                || type.getName().startsWith("net.sf.jsqlparser.parser.")) {
            return;
        }
        if (value instanceof Table table) {
            inspectTable(table);
        }
        if (value instanceof Function function) {
            inspectFunction(function);
        }
        if (value instanceof PlainSelect select
                && ((select.getIntoTables() != null && !select.getIntoTables().isEmpty()) || select.isForUpdate())) {
            throw new SqlRunnerAccessDeniedException();
        }
        // parser 基类只保存语法节点回链；只遍历 SQL 模型自身及模型父类的字段。
        while (type != null && type.getName().startsWith("net.sf.jsqlparser.")
                && !type.getName().startsWith("net.sf.jsqlparser.parser.")) {
            for (Field field : type.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && !field.isSynthetic()) {
                    field.setAccessible(true);
                    inspect(field.get(value), visited, depth + 1);
                }
            }
            type = type.getSuperclass();
        }
    }
}

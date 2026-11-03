package neatlogic.framework.exception.sql;

import neatlogic.framework.exception.core.ApiRuntimeException;

/** 报表 SQL 未满足租户库访问约束时，统一隐藏底层 SQL 和连接信息。 */
public class SqlRunnerAccessDeniedException extends ApiRuntimeException {
    /** 返回不包含数据库配置和 SQL 原文的国际化提示。 */
    public SqlRunnerAccessDeniedException() {
        super("nfes.sqlrunneraccessdeniedexception.sqlrunneraccessdeniedexception");
    }
}

package codedriver.framework.dao.plugin;

import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.fulltextindex.core.FullTextIndexModuleContainer;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.Connection;

/**
 * @Title: FulltextIndexInterceptor
 * @Package: codedriver.framework.dao.plugin
 * @Description: 转换全文检索表成各模块的索引表
 * @author: chenqiwei
 * @date: 2021/3/111:44 上午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class FulltextIndexInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof Executor) {//如果是Executor,则在threadlocal中设置模块名
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            String id = mappedStatement.getId();
            if (id.startsWith("codedriver.module.")) {
                String moduleId = id.split("\\.")[2];
                if (ModuleUtil.getModuleById(moduleId) != null) {
                    FullTextIndexModuleContainer.set(moduleId);
                }
            }
        } else if (invocation.getTarget() instanceof StatementHandler) {
            if (StringUtils.isNotBlank(FullTextIndexModuleContainer.get()) && ModuleUtil.getModuleById(FullTextIndexModuleContainer.get()) != null) {
                StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
                BoundSql boundSql = statementHandler.getBoundSql();
                String originalSql = boundSql.getSql();
                boolean needChange = false;
                if (originalSql.contains("fulltextindex_content")) {
                    originalSql = originalSql.replace("fulltextindex_content", "fulltextindex_content_" + FullTextIndexModuleContainer.get());
                    needChange = true;
                }
                if (originalSql.contains("fulltextindex_offset")) {
                    originalSql = originalSql.replace("fulltextindex_offset", "fulltextindex_offset_" + FullTextIndexModuleContainer.get());
                    needChange = true;
                }
                if (originalSql.contains("fulltextindex_field")) {
                    originalSql = originalSql.replace("fulltextindex_field", "fulltextindex_field_" + FullTextIndexModuleContainer.get());
                    needChange = true;
                }
                if (needChange) {
                    Field field = boundSql.getClass().getDeclaredField("sql");
                    field.setAccessible(true);
                    field.set(boundSql, originalSql);
                }
            }
        }
        return invocation.proceed();
    }

}

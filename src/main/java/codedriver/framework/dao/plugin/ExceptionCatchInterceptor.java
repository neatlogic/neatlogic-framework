/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class ExceptionCatchInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger("deallockAudit");

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取拦截方法的参数
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object result = null;
        try {
            result = invocation.proceed();
        } catch (Exception ex) {
            Throwable targetException = ex;
            //如果是反射抛得异常，则需要拆包，把真实得异常类找出来
            while (targetException instanceof InvocationTargetException) {
                targetException = ((InvocationTargetException) targetException).getTargetException();
            }
            logger.error(targetException.getMessage(), targetException);
            if ("MySQLTransactionRollbackException".equals(targetException.getClass().getSimpleName())) {
                Configuration configuration = ms.getConfiguration();
                Environment environment = configuration.getEnvironment();
                DataSource dataSource = environment.getDataSource();
                Connection connection = null;
                ResultSet resultSet = null;
                PreparedStatement preparedStatement = null;
                try {
                    connection = dataSource.getConnection();
                    String showEngineInnodbStatus = "SHOW ENGINE INNODB STATUS";
                    preparedStatement = connection.prepareStatement(showEngineInnodbStatus);
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        String status = resultSet.getString("Status");
                        if (StringUtils.isNotBlank(status)) {
                            logger.error(status);
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    try {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                        if (preparedStatement != null) {
                            preparedStatement.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            throw targetException;
        }
        return result;
    }
}

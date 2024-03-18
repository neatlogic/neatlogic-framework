/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.dao.plugin;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
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
            if ("MySQLTransactionRollbackException".equals(targetException.getClass().getSimpleName())) {
                Logger logger = LoggerFactory.getLogger("deadlockAudit");
                logger.error(targetException.getMessage(), targetException);
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
            } else if ("MySQLQueryInterruptedException".equals(targetException.getClass().getSimpleName())
                || "TransactionTimedOutException".equals(targetException.getClass().getSimpleName())) {
                Logger logger = LoggerFactory.getLogger("sqlTimeoutAudit");
                // 获取Sql入参
                Object parameterObject = invocation.getArgs()[1];
                logger.error("The error may exist in " + ms.getResource());
                logger.error("The error may involve " + ms.getId() + " -Inline");
                logger.error("SQL: " + ms.getBoundSql(parameterObject).getSql());
                logger.error("parameters: " + JSONObject.toJSONString(parameterObject));
                logger.error(targetException.getMessage(), targetException);
            }
            throw targetException;
        }
        return result;
    }
}

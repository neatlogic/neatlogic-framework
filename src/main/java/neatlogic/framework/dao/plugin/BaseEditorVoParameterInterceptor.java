/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.dto.BaseEditorVo;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class BaseEditorVoParameterInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // 只处理insert和update语句
        if (mappedStatement.getSqlCommandType() != SqlCommandType.INSERT && mappedStatement.getSqlCommandType() != SqlCommandType.UPDATE) {
            return invocation.proceed();
        }
        // 获取Sql入参
        Object parameterObject = invocation.getArgs()[1];
        // 如果没有入参，不做处理
        if (parameterObject == null) {
            return invocation.proceed();
        }
        // 如果入参不是BaseEditorVo类的子类，也不做处理
        if (!(parameterObject instanceof BaseEditorVo)) {
            return invocation.proceed();
        }
        BaseEditorVo baseEditorVo = (BaseEditorVo) parameterObject;
        // 如果fcu字段为null，将当前用户uuid赋值给fcu字段
        if (baseEditorVo.getFcu() == null && UserContext.get() != null) {
            baseEditorVo.setFcu(UserContext.get().getUserUuid());
        }
        // 如果lcu字段为null，将当前用户uuid赋值给lcu字段
        if (baseEditorVo.getLcu() == null && UserContext.get() != null) {
            baseEditorVo.setLcu(UserContext.get().getUserUuid());
        }
        return invocation.proceed();
    }
}

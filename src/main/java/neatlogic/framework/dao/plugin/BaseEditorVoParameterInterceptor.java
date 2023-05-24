/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        if (baseEditorVo.getFcu() == null) {
            baseEditorVo.setFcu(UserContext.get().getUserUuid());
        }
        // 如果lcu字段为null，将当前用户uuid赋值给lcu字段
        if (baseEditorVo.getLcu() == null) {
            baseEditorVo.setLcu(UserContext.get().getUserUuid());
        }
        return invocation.proceed();
    }
}

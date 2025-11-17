/*
 *
 *  *
 *  * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 *  * This file is part of the NeatLogic software.
 *  * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 *  * You may use this file only in compliance with the License.
 *  * See the LICENSE file distributed with this work for the full license text.
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *
 *
 */

package neatlogic.framework.asynchronization.threadlocal;

import org.apache.ibatis.mapping.MappedStatement;

import java.io.Serial;
import java.io.Serializable;

public class InterceptorContext implements Serializable {

    private static final ThreadLocal<InterceptorContext> instance = new ThreadLocal<>();
    @Serial
    private static final long serialVersionUID = -5420998728515359636L;

    private MappedStatement mappedStatement;

    private Object parameter;
    // 判断是否查询了数据库
    private Boolean queryFromDatabase;

    private InterceptorContext() {

    }

    public static InterceptorContext init() {
        InterceptorContext interceptorContext = new InterceptorContext();
        instance.set(interceptorContext);
        return instance.get();
    }

    public static InterceptorContext get() {
        return instance.get();
    }

    public void release() {
        instance.remove();
    }

    public MappedStatement getMappedStatement() {
        return mappedStatement;
    }

    public void setMappedStatement(MappedStatement mappedStatement) {
        this.mappedStatement = mappedStatement;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    public Boolean getQueryFromDatabase() {
        return queryFromDatabase;
    }

    public void setQueryFromDatabase(Boolean queryFromDatabase) {
        this.queryFromDatabase = queryFromDatabase;
    }
}

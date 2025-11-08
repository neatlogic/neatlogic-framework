/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.sqlgenerator;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JoinVo {
    // join类型，LEFT JOIN, JOIN
    private final String operationSymbol;
    private final String schemaName;
    private final String tableName;
    private final String alias;
    private ExpressionVo on;
    private List<ColumnVo> selectColumnList;
    private List<GroupByVo> groupByList;
    private List<OrderByVo> orderByList;
    private List<ExpressionVo> whereExpressionList;

    JoinVo(String operationSymbol, String schemaName, String tableName, String alias, ExpressionVo on) {
        this.operationSymbol = operationSymbol;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.alias = alias;
        this.on = on;
    }

    JoinVo(String operationSymbol, String tableName, String alias, ExpressionVo on) {
        this(operationSymbol, null, tableName, alias, on);
    }
    JoinVo(String operationType, String tableName, String alias) {
        this(operationType, null, tableName, alias, null);
    }

    JoinVo(String operationType, String tableName) {
        this(operationType, null, tableName, null, null);
    }

    public JoinVo withOn(ExpressionVo on) {
        this.on = on;
        return this;
    }

    public List<ColumnVo> getSelectColumnList() {
        return selectColumnList;
    }

    public JoinVo withSelectColumnList(List<ColumnVo> selectColumnList) {
        this.selectColumnList = selectColumnList;
        return this;
    }

    public JoinVo withAddSelectColumnList(List<ColumnVo> selectColumnList) {
        if (this.selectColumnList == null) {
            this.selectColumnList = selectColumnList;
        } else {
            this.selectColumnList.addAll(selectColumnList);
        }
        return this;
    }

    public JoinVo withAddSelectColumn(ColumnVo selectColumn) {
        if (this.selectColumnList == null) {
            this.selectColumnList = new ArrayList<>();
        }
        this.selectColumnList.add(selectColumn);
        return this;
    }

    public List<GroupByVo> getGroupByList() {
        return groupByList;
    }

    public JoinVo withGroupByList(List<GroupByVo> groupByList) {
        this.groupByList = groupByList;
        return this;
    }

    public JoinVo withAddGroupByList(List<GroupByVo> groupByList) {
        if (this.groupByList == null) {
            this.groupByList = groupByList;
        } else {
            this.groupByList.addAll(groupByList);
        }
        return this;
    }

    public JoinVo withAddGroupBy(GroupByVo groupBy) {
        if (this.groupByList == null) {
            this.groupByList = new ArrayList<>();
        }
        this.groupByList.add(groupBy);
        return this;
    }

    public List<OrderByVo> getOrderByList() {
        return orderByList;
    }

    public JoinVo withOrderByList(List<OrderByVo> orderByList) {
        this.orderByList = orderByList;
        return this;
    }

    public JoinVo withAddOrderByList(List<OrderByVo> orderByList) {
        if (this.orderByList == null) {
            this.orderByList = orderByList;
        } else {
            this.orderByList.addAll(orderByList);
        }
        return this;
    }

    public JoinVo withAddOrderBy(OrderByVo orderBy) {
        if (this.orderByList == null) {
            this.orderByList = new ArrayList<>();
        }
        this.orderByList.add(orderBy);
        return this;
    }

    public List<ExpressionVo> getWhereExpressionList() {
        return whereExpressionList;
    }

    public JoinVo withWhereExpressionList(List<ExpressionVo> whereExpressionList) {
        this.whereExpressionList = whereExpressionList;
        return this;
    }

    public JoinVo withAddWhereExpressionList(List<ExpressionVo> whereExpressionList) {
        if (this.whereExpressionList == null) {
            this.whereExpressionList = whereExpressionList;
        } else {
            this.whereExpressionList.addAll(whereExpressionList);
        }
        return this;
    }

    public JoinVo withAddWhereExpression(ExpressionVo whereExpression) {
        if (this.whereExpressionList == null) {
            this.whereExpressionList = new ArrayList<>();
        }
        this.whereExpressionList.add(whereExpression);
        return this;
    }

    public String getOperationSymbol() {
        return operationSymbol;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getAlias() {
        return alias;
    }

    public ExpressionVo getOn() {
        return on;
    }

    @Override
    public String toString() {
        String result = StringUtils.EMPTY;
        if (operationSymbol != null) {
            result += operationSymbol.trim();
            result += StringUtils.SPACE;
        }
        if (schemaName != null) {
            result += schemaName.trim();
            result += ".";
        }
        if (tableName != null) {
            result += tableName.trim();
            result += StringUtils.SPACE;
        }
        if (alias != null) {
            result += alias.trim();
        }
        if (on != null) {
            result += " ON ";
            result += on.toString();
        }
        return result;
    }
}

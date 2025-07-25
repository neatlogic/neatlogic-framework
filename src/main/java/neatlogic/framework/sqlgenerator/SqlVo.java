/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.sqlgenerator;

import java.util.ArrayList;
import java.util.List;

public class SqlVo {
    private List<ColumnVo> selectColumnList;
    private JoinVo fromTable;
    private List<JoinVo> joinList;
    private List<ExpressionVo> whereExpressionList;
    private List<GroupByVo> groupByList;
    private List<OrderByVo> orderByList;
    private LimitVo limit;

    public JoinVo getFromTable() {
        return fromTable;
    }

    public SqlVo withFromTable(JoinVo fromTable) {
        this.fromTable = fromTable;
        return this;
    }

    public List<JoinVo> getJoinList() {
        return joinList;
    }

    public SqlVo withJoinList(List<JoinVo> joinList) {
        this.joinList = joinList;
        return this;
    }

    public SqlVo withAddJoinList(List<JoinVo> joinList) {
        if (this.joinList == null) {
            this.joinList = joinList;
        } else {
            this.joinList.addAll(joinList);
        }
        return this;
    }

    public SqlVo withAddJoin(JoinVo join) {
        if (this.joinList == null) {
            this.joinList = new ArrayList<>();
        }
        this.joinList.add(join);
        return this;
    }

    public List<ColumnVo> getSelectColumnList() {
        return selectColumnList;
    }

    public SqlVo withSelectColumnList(List<ColumnVo> selectColumnList) {
        this.selectColumnList = selectColumnList;
        return this;
    }

    public SqlVo withAddSelectColumnList(List<ColumnVo> selectColumnList) {
        if (this.selectColumnList == null) {
            this.selectColumnList = selectColumnList;
        } else {
            this.selectColumnList.addAll(selectColumnList);
        }
        return this;
    }

    public SqlVo withAddSelectColumn(ColumnVo selectColumn) {
        if (this.selectColumnList == null) {
            this.selectColumnList = new ArrayList<>();
        }
        this.selectColumnList.add(selectColumn);
        return this;
    }

    public List<GroupByVo> getGroupByList() {
        return groupByList;
    }

    public SqlVo withGroupByList(List<GroupByVo> groupByList) {
        this.groupByList = groupByList;
        return this;
    }

    public SqlVo withAddGroupByList(List<GroupByVo> groupByList) {
        if (this.groupByList == null) {
            this.groupByList = groupByList;
        } else {
            this.groupByList.addAll(groupByList);
        }
        return this;
    }

    public SqlVo withAddGroupBy(GroupByVo groupBy) {
        if (this.groupByList == null) {
            this.groupByList = new ArrayList<>();
        }
        this.groupByList.add(groupBy);
        return this;
    }

    public List<OrderByVo> getOrderByList() {
        return orderByList;
    }

    public SqlVo withOrderByList(List<OrderByVo> orderByList) {
        this.orderByList = orderByList;
        return this;
    }

    public SqlVo withAddOrderByList(List<OrderByVo> orderByList) {
        if (this.orderByList == null) {
            this.orderByList = orderByList;
        } else {
            this.orderByList.addAll(orderByList);
        }
        return this;
    }

    public SqlVo withAddOrderBy(OrderByVo orderBy) {
        if (this.orderByList == null) {
            this.orderByList = new ArrayList<>();
        }
        this.orderByList.add(orderBy);
        return this;
    }

    public List<ExpressionVo> getWhereExpressionList() {
        return whereExpressionList;
    }

    public SqlVo withWhereExpressionList(List<ExpressionVo> whereExpressionList) {
        this.whereExpressionList = whereExpressionList;
        return this;
    }

    public SqlVo withAddWhereExpressionList(List<ExpressionVo> whereExpressionList) {
        if (this.whereExpressionList == null) {
            this.whereExpressionList = whereExpressionList;
        } else {
            this.whereExpressionList.addAll(whereExpressionList);
        }
        return this;
    }

    public SqlVo withAddWhereExpression(ExpressionVo whereExpression) {
        if (this.whereExpressionList == null) {
            this.whereExpressionList = new ArrayList<>();
        }
        this.whereExpressionList.add(whereExpression);
        return this;
    }

    public LimitVo getLimit() {
        return limit;
    }

    public SqlVo withLimit(LimitVo limit) {
        this.limit = limit;
        return this;
    }
}

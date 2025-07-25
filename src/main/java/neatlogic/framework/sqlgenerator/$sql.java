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

import neatlogic.framework.util.TimeUtil;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>基本用法</p>
 * <p>PlainSelect plainSelect = $sql.from("tenant", "a");</p>
 * <p>$sql.addSelectColumn(plainSelect, "a.name");</p>
 * <p>$sql.addSelectColumn(plainSelect, $sql.fun("count", "b.module_group"), "moduleGroupCount");</p>
 * <p>$sql.addJoin(plainSelect, "left join", "tenant_modulegroup", "b", $sql.exp("b.tenant_uuid", "=", "a.uuid"));</p>
 * <p>$sql.addWhereExpression(plainSelect, $sql.exp("a.is_active", "=", 1));</p>
 * <p>$sql.addGroupBy(plainSelect, "a.name");</p>
 * <p>$sql.addOrderBy(plainSelect, "a.name", "desc");</p>
 * <p>$sql.setLimit(plainSelect, 3, 20);</p>
 * <p>输出结果：</p>
 * <p>SELECT</p>
 * <p>  a.name,</p>
 * <p>  count(b.module_group) AS moduleGroupCount</p>
 * <p>FROM tenant a</p>
 * <p>LEFT JOIN tenant_modulegroup b ON b.tenant_uuid = a.uuid</p>
 * <p>WHERE a.is_active = 1</p>
 * <p>GROUP BY a.name</p>
 * <p>ORDER BY a.name DESC</p>
 * <p>LIMIT 3, 20</p>
 */
public class $sql {
    // 布尔运算
    public static final String BOOLEAN_OPERATION = "booleanOperation";
    // 逻辑运算
    public static final String COMPARATIVE_OPERATION = "comparativeOperation";

    public static JoinVo join(String operationSymbol, String schemaName, String tableName, String alias, ExpressionVo on) {
        return new JoinVo(operationSymbol, schemaName, tableName, alias, on);
    }

    public static JoinVo join(String operationSymbol, String tableName, String alias, ExpressionVo on) {
        return new JoinVo(operationSymbol, null, tableName, alias, on);
    }

    public static JoinVo join(String operationSymbol, String tableName, String alias) {
        return new JoinVo(operationSymbol,null,  tableName, alias, null);
    }

    public static ExpressionVo exp(String leftParenthesis, ExpressionVo leftExpressionVo, String operationSymbol, ExpressionVo rightExpressionVo, String rightParenthesis) {
        return new ExpressionVo(leftParenthesis, leftExpressionVo, operationSymbol, rightExpressionVo, rightParenthesis);
    }

    public static ExpressionVo exp(ExpressionVo leftExpressionVo, String operationType, ExpressionVo rightExpressionVo) {
        return exp(null, leftExpressionVo, operationType, rightExpressionVo, null);
    }

    public static ExpressionVo exp(String leftColumn, String operationSymbol, String rightColumn) {
        return new ExpressionVo(leftColumn, operationSymbol, rightColumn);
    }

    public static ExpressionVo exp(String leftColumn, String operationSymbol, Integer rightValue) {
        return new ExpressionVo(leftColumn, operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(String leftColumn, String operationSymbol, Long rightValue) {
        return new ExpressionVo(leftColumn, operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(String leftColumn, String operationSymbol, Double rightValue) {
        return new ExpressionVo(leftColumn, operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(String leftColumn, String operationSymbol, List<?> rightValue) {
        return new ExpressionVo(leftColumn, operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(String leftColumn, String operationSymbol, Date date) {
        return new ExpressionVo(leftColumn, operationSymbol, new ValueVo(date));
    }

    public static ExpressionVo exp(String leftColumn, String operationSymbol, ValueVo rightValueExpression) {
        return new ExpressionVo(leftColumn, operationSymbol, rightValueExpression);
    }

    public static ExpressionVo exp(String leftColumn, String operationSymbol) {
        return new ExpressionVo(leftColumn, operationSymbol, new ValueVo(""));
    }

    public static ExpressionVo exp(Integer leftValue, String operationSymbol, Integer rightValue) {
        return new ExpressionVo(new ValueVo(leftValue), operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(Integer leftValue, String operationSymbol, Long rightValue) {
        return new ExpressionVo(new ValueVo(leftValue), operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(Integer leftValue, String operationSymbol, Double rightValue) {
        return new ExpressionVo(new ValueVo(leftValue), operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(Long leftValue, String operationSymbol, Integer rightValue) {
        return new ExpressionVo(new ValueVo(leftValue), operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(Long leftValue, String operationSymbol, Long rightValue) {
        return new ExpressionVo(new ValueVo(leftValue), operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(Long leftValue, String operationSymbol, Double rightValue) {
        return new ExpressionVo(new ValueVo(leftValue), operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(Double leftValue, String operationSymbol, Integer rightValue) {
        return new ExpressionVo(new ValueVo(leftValue), operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(Double leftValue, String operationSymbol, Long rightValue) {
        return new ExpressionVo(new ValueVo(leftValue), operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(Double leftValue, String operationSymbol, Double rightValue) {
        return new ExpressionVo(new ValueVo(leftValue), operationSymbol, new ValueVo(rightValue));
    }

    public static ExpressionVo exp(ValueVo leftValueExpression, String operationSymbol, ValueVo rightValueExpression) {
        return new ExpressionVo(leftValueExpression, operationSymbol, rightValueExpression);
    }

    public static ValueVo value(String strValue) {
        return new ValueVo(strValue);
    }

    public static ValueVo value(Integer intValue) {
        return new ValueVo(intValue);
    }

    public static ValueVo value(Long longValue) {
        return new ValueVo(longValue);
    }

    public static ValueVo value(Double doubleValue) {
        return new ValueVo(doubleValue);
    }

    public static ValueVo value(List list) {
        return new ValueVo(list);
    }

    public static FunctionVo fun(String funName, Object ... parameters) {
        return new FunctionVo(funName, parameters);
    }

    public static PlainSelect addSql(SqlVo sqlVo) {
        PlainSelect plainSelect = new PlainSelect();
        addSql(plainSelect, sqlVo);
        return plainSelect;
    }

    public static void addSql(PlainSelect plainSelect, SqlVo sqlVo) {
        if (plainSelect == null || sqlVo == null) {
            return;
        }
        List<GroupByVo> groupByList = new ArrayList<>();
        List<OrderByVo> orderByList = new ArrayList<>();
        List<ColumnVo> selectColumnList = new ArrayList<>();
        List<ExpressionVo> whereExpressionList = new ArrayList<>();
        JoinVo fromTable = sqlVo.getFromTable();
        if (fromTable != null) {
            if (CollectionUtils.isNotEmpty(fromTable.getGroupByList())) {
                groupByList.addAll(fromTable.getGroupByList());
            }
            if (CollectionUtils.isNotEmpty(fromTable.getOrderByList())) {
                orderByList.addAll(fromTable.getOrderByList());
            }
            if (CollectionUtils.isNotEmpty(fromTable.getSelectColumnList())) {
                selectColumnList.addAll(fromTable.getSelectColumnList());
            }
            if (CollectionUtils.isNotEmpty(fromTable.getWhereExpressionList())) {
                whereExpressionList.addAll(fromTable.getWhereExpressionList());
            }
            from(plainSelect, fromTable.getSchemaName(), fromTable.getTableName(), fromTable.getAlias());
        }
        if (CollectionUtils.isNotEmpty(sqlVo.getGroupByList())) {
            groupByList.addAll(sqlVo.getGroupByList());
        }
        if (CollectionUtils.isNotEmpty(sqlVo.getOrderByList())) {
            orderByList.addAll(sqlVo.getOrderByList());
        }
        if (CollectionUtils.isNotEmpty(sqlVo.getSelectColumnList())) {
            selectColumnList.addAll(sqlVo.getSelectColumnList());
        }
        if (CollectionUtils.isNotEmpty(sqlVo.getWhereExpressionList())) {
            whereExpressionList.addAll(sqlVo.getWhereExpressionList());
        }
        List<JoinVo> joinList = sqlVo.getJoinList();
        for (JoinVo joinVo : joinList) {
            if (CollectionUtils.isNotEmpty(joinVo.getGroupByList())) {
                groupByList.addAll(joinVo.getGroupByList());
            }
            if (CollectionUtils.isNotEmpty(joinVo.getOrderByList())) {
                orderByList.addAll(joinVo.getOrderByList());
            }
            if (CollectionUtils.isNotEmpty(joinVo.getSelectColumnList())) {
                selectColumnList.addAll(joinVo.getSelectColumnList());
            }
            if (CollectionUtils.isNotEmpty(joinVo.getWhereExpressionList())) {
                whereExpressionList.addAll(joinVo.getWhereExpressionList());
            }
            addJoin(plainSelect, joinVo);
        }
        if (CollectionUtils.isNotEmpty(selectColumnList)) {
            for (ColumnVo selectColumn : selectColumnList) {
                if (StringUtils.isNotBlank(selectColumn.getName())) {
                    addSelectColumn(plainSelect, selectColumn.getName(), selectColumn.getAlias());
                } else if (selectColumn.getFunction() != null) {
                    addSelectColumn(plainSelect, selectColumn.getFunction(), selectColumn.getAlias());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(whereExpressionList)) {
            for (ExpressionVo whereExpression : whereExpressionList) {
                addWhereExpression(plainSelect, whereExpression);
            }
        }
        if (CollectionUtils.isNotEmpty(groupByList)) {
            groupByList.sort(Comparator.comparingInt(GroupByVo::getSort));
            for (GroupByVo groupByVo : groupByList) {
                addGroupBy(plainSelect, groupByVo.getColumnName());
            }
        }
        if (CollectionUtils.isNotEmpty(orderByList)) {
            orderByList.sort(Comparator.comparingInt(OrderByVo::getSort));
            for (OrderByVo orderByVo : orderByList) {
                if (StringUtils.isNotBlank(orderByVo.getColumnName())) {
                    addOrderBy(plainSelect, orderByVo.getColumnName(), orderByVo.getAsc());
                } else if (orderByVo.getFunction() != null) {
                    addOrderBy(plainSelect, orderByVo.getFunction(), orderByVo.getAsc());
                }
            }
        }
        if (sqlVo.getLimit() != null) {
            setLimit(plainSelect, sqlVo.getLimit().getOffset(), sqlVo.getLimit().getRowCount());
        }
    }

    public static void from(PlainSelect plainSelect, String tableName) {
        if (StringUtils.isNotBlank(tableName)) {
            String schemaName = null;
            String alias = null;
            tableName = tableName.trim();
            if (tableName.contains(".")) {
                String[] split = tableName.split("\\.");
                schemaName = split[0];
                tableName = split[1];
            }
            if (tableName.contains(" ")) {
                String[] split = tableName.split(" ");
                tableName = split[0];
                alias = split[1];
            }
            from(plainSelect, schemaName, tableName, alias);
        }
    }

    public static void from(PlainSelect plainSelect, String tableName, String alias) {
        if (StringUtils.isNotBlank(tableName)) {
            String schemaName = null;
            tableName = tableName.trim();
            if (tableName.contains(".")) {
                String[] split = tableName.split("\\.");
                schemaName = split[0];
                tableName = split[1];
            }
            from(plainSelect, schemaName, tableName, alias);
        }
    }

    public static void from(PlainSelect plainSelect, String schemaName, String tableName, String alias) {
        if (StringUtils.isNotBlank(tableName)) {
            Table table = new Table(tableName);
            if (StringUtils.isNotBlank(schemaName)) {
                table.withSchemaName(schemaName.trim());
            }
            if (StringUtils.isNotBlank(alias)) {
                table.withAlias(new Alias(alias.trim()).withUseAs(false));
            }
            plainSelect.withFromItem(table);
        }
    }

    public static PlainSelect from(String tableName) {
        PlainSelect plainSelect = new PlainSelect();
        from(plainSelect, tableName);
        return plainSelect;
    }

    public static PlainSelect from(String tableName, String alias) {
        PlainSelect plainSelect = new PlainSelect();
        from(plainSelect, tableName, alias);
        return plainSelect;
    }

    public static PlainSelect from(String schemaName, String tableName, String alias) {
        PlainSelect plainSelect = new PlainSelect();
        from(plainSelect, schemaName, tableName, alias);
        return plainSelect;
    }

    public static void addSelectColumn(PlainSelect plainSelect, String columnName) {
        addSelectColumn(plainSelect, columnName, null);
    }

    public static void addSelectColumn(PlainSelect plainSelect, String columnName, String alias) {
        if (plainSelect == null || columnName == null) {
            return;
        }
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(new Column(columnName.trim()));
        if (StringUtils.isNotBlank(alias)) {
            selectExpressionItem.withAlias(new Alias(alias));
        }
        plainSelect.addSelectItems(selectExpressionItem);
    }

    public static void setSelectColumn(PlainSelect plainSelect, String columnName) {
        setSelectColumn(plainSelect, columnName, null);
    }

    public static void setSelectColumn(PlainSelect plainSelect, String columnName, String alias) {
        if (plainSelect == null || columnName == null) {
            return;
        }
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(new Column(columnName.trim()));
        if (StringUtils.isNotBlank(alias)) {
            selectExpressionItem.withAlias(new Alias(alias));
        }
        List<SelectItem> selectItemList = new ArrayList<>();
        selectItemList.add(selectExpressionItem);
        plainSelect.setSelectItems(selectItemList);
    }

    public static void addSelectColumn(PlainSelect plainSelect, ValueVo valueVo) {
        addSelectColumn(plainSelect, valueVo, null);
    }

    public static void addSelectColumn(PlainSelect plainSelect, ValueVo valueVo, String alias) {
        if (plainSelect == null || valueVo == null) {
            return;
        }
        Object obj = parseValue(valueVo);
        if (obj instanceof Expression) {
            SelectExpressionItem selectExpressionItem = new SelectExpressionItem((Expression) obj);
            if (StringUtils.isNotBlank(alias)) {
                selectExpressionItem.withAlias(new Alias(alias));
            }
            plainSelect.addSelectItems(selectExpressionItem);
        } else if (obj instanceof ExpressionList) {
            ExpressionList expressionList = (ExpressionList) obj;
            List<Expression> expressions = expressionList.getExpressions();
            if (CollectionUtils.isNotEmpty(expressions)) {
                List<SelectItem> selectItemList = new ArrayList<>();
                for (Expression expression : expressions) {
                    SelectExpressionItem selectExpressionItem = new SelectExpressionItem(expression);
                    if (StringUtils.isNotBlank(alias)) {
                        selectExpressionItem.withAlias(new Alias(alias));
                    }
                    selectItemList.add(selectExpressionItem);
                }
                plainSelect.addSelectItems(selectItemList);
            }
        }
    }

    public static void setSelectColumn(PlainSelect plainSelect, ValueVo valueVo) {
        setSelectColumn(plainSelect, valueVo, null);
    }

    public static void setSelectColumn(PlainSelect plainSelect, ValueVo valueVo, String alias) {
        if (plainSelect == null || valueVo == null) {
            return;
        }
        Object obj = parseValue(valueVo);
        if (obj != null) {
            if (obj instanceof Expression) {
                SelectExpressionItem selectExpressionItem = new SelectExpressionItem((Expression) obj);
                if (StringUtils.isNotBlank(alias)) {
                    selectExpressionItem.withAlias(new Alias(alias));
                }
                List<SelectItem> selectItemList = new ArrayList<>();
                selectItemList.add(selectExpressionItem);
                plainSelect.setSelectItems(selectItemList);
            } else if (obj instanceof ExpressionList) {
                ExpressionList expressionList = (ExpressionList) obj;
                List<Expression> expressions = expressionList.getExpressions();
                if (CollectionUtils.isNotEmpty(expressions)) {
                    List<SelectItem> selectItemList = new ArrayList<>();
                    for (Expression expression : expressions) {
                        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(expression);
                        if (StringUtils.isNotBlank(alias)) {
                            selectExpressionItem.withAlias(new Alias(alias));
                        }
                        selectItemList.add(selectExpressionItem);
                    }
                    plainSelect.setSelectItems(selectItemList);
                }
            }
        }
    }

    public static void addSelectColumn(PlainSelect plainSelect, FunctionVo functionVo) {
        addSelectColumn(plainSelect, functionVo, null);
    }

    public static void addSelectColumn(PlainSelect plainSelect, FunctionVo functionVo, String alias) {
        if (plainSelect == null || functionVo == null) {
            return;
        }
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(parseFunction(functionVo));
        if (StringUtils.isNotBlank(alias)) {
            selectExpressionItem.withAlias(new Alias(alias));
        }
        plainSelect.addSelectItems(selectExpressionItem);
    }

    public static void setSelectColumn(PlainSelect plainSelect, FunctionVo functionVo) {
        setSelectColumn(plainSelect, functionVo, null);
    }

    public static void setSelectColumn(PlainSelect plainSelect, FunctionVo functionVo, String alias) {
        if (plainSelect == null || functionVo == null) {
            return;
        }
        SelectExpressionItem selectExpressionItem = new SelectExpressionItem(parseFunction(functionVo));
        if (StringUtils.isNotBlank(alias)) {
            selectExpressionItem.withAlias(new Alias(alias));
        }
        List<SelectItem> selectItemList = new ArrayList<>();
        selectItemList.add(selectExpressionItem);
        plainSelect.setSelectItems(selectItemList);
    }

    public static void addJoin(PlainSelect plainSelect, String operationSymbol, String schemaName, String tableName, String alias, ExpressionVo on) {
        addJoin(plainSelect, new JoinVo(operationSymbol, schemaName, tableName, alias, on));
    }

    public static void addJoin(PlainSelect plainSelect, String operationSymbol, String tableName, String alias, ExpressionVo on) {
        addJoin(plainSelect, new JoinVo(operationSymbol, null, tableName, alias, on));
    }

    public static void addJoin(PlainSelect plainSelect, JoinVo joinVo) {
        if (plainSelect == null || joinVo == null) {
            return;
        }
        Join join = new Join();
        if (Objects.equals(joinVo.getOperationSymbol().toLowerCase().trim(), "left join")) {
            join.withLeft(true);
        }
        Table table = new Table(joinVo.getTableName().trim());
        if (StringUtils.isNotBlank(joinVo.getSchemaName())) {
            table.withSchemaName(joinVo.getSchemaName().trim());
        }
        if (StringUtils.isNotBlank(joinVo.getAlias())) {
            table.withAlias(new Alias(joinVo.getAlias().trim()).withUseAs(false));
        }
        join.withRightItem(table);
        ExpressionVo on = joinVo.getOn();
        if (on != null) {
            Expression expression = parseExpression(on);
            if (expression != null) {
                join.addOnExpression(expression);
            }
        }
        plainSelect.addJoins(join);
    }

    public static void addJoinList(PlainSelect plainSelect, List<JoinVo> joinList) {
        if (plainSelect == null || CollectionUtils.isEmpty(joinList)) {
            return;
        }
        for (JoinVo joinVo : joinList) {
            addJoin(plainSelect, joinVo);
        }
    }

    public static void addWhereExpression(PlainSelect plainSelect, ExpressionVo expressionVo) {
        addWhereExpression(plainSelect, "and", expressionVo);
    }

    public static void addWhereExpression(PlainSelect plainSelect, String operationSymbol, ExpressionVo expressionVo) {
        if (plainSelect == null || expressionVo == null || operationSymbol == null) {
            return;
        }
        Expression expression = parseExpression(expressionVo);
        if (expression != null) {
            Expression where = plainSelect.getWhere();
            if (where != null) {
                if (Objects.equals(operationSymbol.toLowerCase().trim(), "or")) {
                    plainSelect.setWhere(new OrExpression(where, expression));
                } else {
                    plainSelect.setWhere(new AndExpression(where, expression));
                }
            } else {
                plainSelect.setWhere(expression);
            }
        }
    }

    public static void addWhereExpressionList(PlainSelect plainSelect, String operationSymbol, List<ExpressionVo> expressionList) {
        if (plainSelect == null || CollectionUtils.isEmpty(expressionList) || operationSymbol == null) {
            return;
        }
        for (ExpressionVo expressionVo : expressionList) {
            addWhereExpression(plainSelect, operationSymbol, expressionVo);
        }
    }

    public static void addWhereExpressionList(PlainSelect plainSelect, List<ExpressionVo> expressionList) {
        addWhereExpressionList(plainSelect, "and", expressionList);
    }

    public static void addGroupBy(PlainSelect plainSelect, String columnName) {
        if (plainSelect == null || columnName == null) {
            return;
        }
        plainSelect.addGroupByColumnReference(new Column(columnName));
    }

    public static void addGroupBy(PlainSelect plainSelect, Column column) {
        if (plainSelect == null || column == null) {
            return;
        }
        plainSelect.addGroupByColumnReference(column);
    }

    public static void addOrderBy(PlainSelect plainSelect, String columnName) {
        addOrderBy(plainSelect, columnName, null);
    }
    public static void addOrderBy(PlainSelect plainSelect, String columnName, String asc) {
        if (plainSelect == null || columnName == null) {
            return;
        }
        OrderByElement orderByElement = new OrderByElement().withExpression(new Column(columnName.trim()));
        if (StringUtils.isNotBlank(asc) && Objects.equals(asc.trim().toLowerCase(), "desc")) {
            orderByElement.withAsc(false);
        }
        plainSelect.addOrderByElements(orderByElement);
    }

    public static void addOrderBy(PlainSelect plainSelect, FunctionVo functionVo) {
        addOrderBy(plainSelect, functionVo, "asc");
    }

    public static void addOrderBy(PlainSelect plainSelect, FunctionVo functionVo, String asc) {
        if (plainSelect == null || functionVo == null || asc == null) {
            return;
        }
        OrderByElement orderByElement = new OrderByElement().withExpression(parseFunction(functionVo));
        if (Objects.equals(asc.trim().toLowerCase(), "desc")) {
            orderByElement.withAsc(false);
        }
        plainSelect.addOrderByElements(orderByElement);
    }

    public static void setLimit(PlainSelect plainSelect, int startNum, int pageSize) {
        if (plainSelect == null) {
            return;
        }
        plainSelect.setLimit(new Limit().withOffset(new LongValue(startNum)).withRowCount(new LongValue(pageSize)));
    }

    public static void setLimit(PlainSelect plainSelect, int pageSize) {
        setLimit(plainSelect, 0, pageSize);
    }

    private static Expression parseExpression(ExpressionVo expressionVo) {
        Expression resultExpression = null;
        String operationSymbol = expressionVo.getOperationSymbol().toLowerCase().trim();
        if (Objects.equals(expressionVo.getType(), BOOLEAN_OPERATION)) {
            if (Objects.equals(operationSymbol, "and")) {
                ExpressionVo leftExpression = expressionVo.getLeftExpression();
                ExpressionVo rightExpression = expressionVo.getRightExpression();
                if (leftExpression != null && rightExpression != null) {
                    Expression expression1 = parseExpression(leftExpression);
                    Expression expression2 = parseExpression(rightExpression);
                    resultExpression = new AndExpression(expression1, expression2);
                } else if (leftExpression != null) {
                    resultExpression = parseExpression(leftExpression);
                } else if (rightExpression != null) {
                    resultExpression = parseExpression(rightExpression);
                }
            } else if (Objects.equals(operationSymbol, "or")) {
                ExpressionVo leftExpression = expressionVo.getLeftExpression();
                ExpressionVo rightExpression = expressionVo.getRightExpression();
                if (leftExpression != null && rightExpression != null) {
                    Expression expression1 = parseExpression(leftExpression);
                    Expression expression2 = parseExpression(rightExpression);
                    resultExpression = new OrExpression(expression1, expression2);
                } else if (leftExpression != null) {
                    resultExpression = parseExpression(leftExpression);
                } else if (rightExpression != null) {
                    resultExpression = parseExpression(rightExpression);
                }
            }
        } else if (Objects.equals(expressionVo.getType(), COMPARATIVE_OPERATION)) {
            Expression leftExpression = null;
            if (StringUtils.isNotBlank(expressionVo.getLeftColumn())) {
                leftExpression = new Column(expressionVo.getLeftColumn());
            } else if (expressionVo.getLeftValueExpression() != null) {
                Object leftObj = parseValue(expressionVo.getLeftValueExpression());
                if (leftObj instanceof Expression) {
                    leftExpression = (Expression) leftObj;
                }
            }
            Expression rightExpression = null;
            ExpressionList rightExpressionList = null;
            String rightColumn = expressionVo.getRightColumn();
            if (StringUtils.isNotBlank(rightColumn)) {
                rightExpression = new Column(rightColumn);
            } else if (expressionVo.getRightValueExpression() != null) {
                Object rightObj = parseValue(expressionVo.getRightValueExpression());
                if (rightObj instanceof Expression) {
                    rightExpression = (Expression) rightObj;
                } else if (rightObj instanceof ExpressionList) {
                    rightExpressionList = (ExpressionList) rightObj;
                }
            }
            if (Objects.equals(operationSymbol, "=")) {
                resultExpression = new EqualsTo(leftExpression, rightExpression);
            } else if (Objects.equals(operationSymbol, "!=")) {
                resultExpression = new NotEqualsTo(leftExpression, rightExpression);
            } else if (Objects.equals(operationSymbol, ">")) {
                resultExpression = new GreaterThan().withLeftExpression(leftExpression).withRightExpression(rightExpression);
            } else if (Objects.equals(operationSymbol, ">=")) {
                resultExpression = new GreaterThanEquals().withLeftExpression(leftExpression).withRightExpression(rightExpression);
            } else if (Objects.equals(operationSymbol, "<")) {
                resultExpression = new MinorThan().withLeftExpression(leftExpression).withRightExpression(rightExpression);
            } else if (Objects.equals(operationSymbol, "<=")) {
                resultExpression = new MinorThanEquals().withLeftExpression(leftExpression).withRightExpression(rightExpression);
            } else if (Objects.equals(operationSymbol, "like")) {
                resultExpression = new LikeExpression().withLeftExpression(leftExpression).withRightExpression(rightExpression);
            } else if (Objects.equals(operationSymbol, "not like")) {
                resultExpression = new LikeExpression().withNot(true).withLeftExpression(leftExpression).withRightExpression(rightExpression);
            } else if (Objects.equals(operationSymbol, "in")) {
                if (rightExpressionList == null) {
                    if (StringUtils.isNotBlank(rightColumn)) {
                        rightExpressionList = string2ExpressionList(rightColumn);
                    } else if (rightExpression != null) {
                        rightExpressionList = new ExpressionList();
                        rightExpressionList.addExpressions(rightExpression);
                    }
                }
                resultExpression = new InExpression(leftExpression, rightExpressionList);
            } else if (Objects.equals(operationSymbol, "not in")) {
                if (rightExpressionList == null) {
                    if (StringUtils.isNotBlank(rightColumn)) {
                        rightExpressionList = string2ExpressionList(rightColumn);
                    } else if (rightExpression != null) {
                        rightExpressionList = new ExpressionList();
                        rightExpressionList.addExpressions(rightExpression);
                    }
                }
                resultExpression = new InExpression(leftExpression, rightExpressionList).withNot(true);
            } else if (Objects.equals(operationSymbol, "is null")) {
                resultExpression = new IsNullExpression().withLeftExpression(leftExpression);
            } else if (Objects.equals(operationSymbol, "is not null")) {
                resultExpression = new IsNullExpression().withLeftExpression(leftExpression).withNot(true);
            }
        }
        if (resultExpression != null) {
            if (expressionVo.getLeftParenthesis() != null && expressionVo.getRightParenthesis() != null) {
                if (Objects.equals(expressionVo.getLeftParenthesis().trim(), "(")
                        && Objects.equals(expressionVo.getRightParenthesis().trim(), ")")) {
                    return new Parenthesis(resultExpression);
                }
            }
            return resultExpression;
        }
        return null;
    }

    private static ExpressionList string2ExpressionList(String expressionListStr) {
        if (StringUtils.isNotBlank(expressionListStr)) {
            ExpressionList expressionList = new ExpressionList();
            expressionListStr = expressionListStr.trim();
            if (expressionListStr.startsWith("(") && expressionListStr.endsWith(")")) {
                expressionListStr = expressionListStr.substring(1, expressionListStr.length() - 1);
                if (expressionListStr.contains(",")) {
                    String[] split = expressionListStr.split(",");
                    for (String str : split) {
                        str = str.trim();
                        if (NumberUtils.isCreatable(str)) {
                            expressionList.addExpressions(new DoubleValue(str));
                        } else {
                            expressionList.addExpressions(new StringValue(str));
                        }
                    }
                } else {
                    if (NumberUtils.isCreatable(expressionListStr)) {
                        expressionList.addExpressions(new DoubleValue(expressionListStr));
                    } else {
                        expressionList.addExpressions(new StringValue(expressionListStr));
                    }
                }
            } else {
                expressionList.addExpressions(new StringValue(expressionListStr));
            }
            return expressionList;
        }
        return null;
    }

    private static Object parseValue(ValueVo valueVo) {
        if (valueVo.getStrValue() != null) {
            return new StringValue(valueVo.getStrValue());
        } else if (valueVo.getIntValue() != null) {
            return new LongValue(valueVo.getIntValue());
        } else if (valueVo.getLongValue() != null) {
            return new LongValue(valueVo.getLongValue());
        } else if (valueVo.getDoubleValue() != null) {
            return new DoubleValue(valueVo.getDoubleValue().toString());
        } else if (valueVo.getDate() != null) {
            return new StringValue(new SimpleDateFormat(TimeUtil.YYYY_MM_DD_HH_MM_SS).format(valueVo.getDate()));
        } else if (valueVo.getList() != null) {
            ExpressionList expressionList = new ExpressionList();
            List<?> list = valueVo.getList();
            for (Object obj : list) {
                if (obj != null) {
                    if (obj instanceof String) {
                        expressionList.addExpressions(new StringValue((String) obj));
                    } else if (obj instanceof Integer) {
                        expressionList.addExpressions(new LongValue((Integer) obj));
                    } else if (obj instanceof Long) {
                        expressionList.addExpressions(new LongValue((Long) obj));
                    } else if (obj instanceof Double) {
                        expressionList.addExpressions(new DoubleValue().withValue((Double) obj));
                    }
                }
            }
            return expressionList;
        }
        return null;
    }

    private static Function parseFunction(FunctionVo functionVo) {
        if (functionVo != null) {
            Function function = new Function();
            function.withName(functionVo.getName());
            function.withDistinct(functionVo.isDistinct());
            Object[] parameters = functionVo.getParameters();
            if (parameters != null && parameters.length > 0) {
                ExpressionList expressionList = new ExpressionList();
                for (Object parameter : parameters) {
                    if (parameter != null) {
                        if (parameter instanceof String) {
                            expressionList.addExpressions(new Column((String) parameter));
                        } else if (parameter instanceof Integer) {
                            Object obj = parseValue(new ValueVo((Integer) parameter));
                            if (obj instanceof Expression) {
                                expressionList.addExpressions((Expression) obj);
                            }
                        } else if (parameter instanceof Long) {
                            Object obj = parseValue(new ValueVo((Long) parameter));
                            if (obj instanceof Expression) {
                                expressionList.addExpressions((Expression) obj);
                            }
                        } else if (parameter instanceof Double) {
                            Object obj = parseValue(new ValueVo((Double) parameter));
                            if (obj instanceof Expression) {
                                expressionList.addExpressions((Expression) obj);
                            }
                        } else if (parameter instanceof Date) {
                            Object obj = parseValue(new ValueVo((Date) parameter));
                            if (obj instanceof Expression) {
                                expressionList.addExpressions((Expression) obj);
                            }
                        } else if (parameter instanceof List) {
                            Object obj = parseValue(new ValueVo((List<?>) parameter));
                            if (obj instanceof ExpressionList) {
                                expressionList.addExpressions(((ExpressionList) obj).getExpressions());
                            }
                        } else if (parameter instanceof ValueVo) {
                            Object obj = parseValue((ValueVo) parameter);
                            if (obj instanceof Expression) {
                                expressionList.addExpressions((Expression) obj);
                            } else if (obj instanceof ExpressionList) {
                                expressionList.addExpressions(((ExpressionList) obj).getExpressions());
                            }
                        } else if (parameter instanceof ExpressionVo) {
                            Expression expression = parseExpression((ExpressionVo) parameter);
                            if (expression != null) {
                                expressionList.addExpressions(expression);
                            }
                        } else if (parameter instanceof FunctionVo) {
                            Function function1 = parseFunction((FunctionVo) parameter);
                            if (function1 != null) {
                                expressionList.addExpressions(function1);
                            }
                        } else if (parameter instanceof Expression) {
                            expressionList.addExpressions((Expression) parameter);
                        } else {
                            Object obj = parseValue(new ValueVo(parameter.toString()));
                            if (obj instanceof Expression) {
                                expressionList.addExpressions((Expression) obj);
                            }
                        }
                    }
                }
                function.setParameters(expressionList);
            }
            return function;
        }
        return null;
    }
}

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

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class $sqlTest {

    public static void main(String[] args) {
        List<String> methodNameList = getMethodNameList($sql.class);
        testSql(methodNameList);
        testForm(methodNameList);
        testSetDistinct(methodNameList);
        testSetSelectColumn(methodNameList);
        testAddSelectColumn(methodNameList);
        testAddJoin(methodNameList);
        testAddWhereExpressionList(methodNameList);
        testAddGroupBy(methodNameList);
        testAddOrderBy(methodNameList);
        testSetLimit(methodNameList);
        testValue(methodNameList);
        testExp(methodNameList);
        testFun(methodNameList);
        if (CollectionUtils.isNotEmpty(methodNameList)) {
            System.out.println("$sql类中以下方法没有被测试：");
            for (String methodName : methodNameList) {
                System.out.println(methodName);
            }
        } else {
            System.out.println("$sql类中所有public static方法测试成功");
        }
//        PlainSelect plainSelect = $sql.from("tenant", "a");
//        $sql.addSelectColumn(plainSelect, "a.name");
//        $sql.addSelectColumn(plainSelect, $sql.fun("count", "b.module_group"), "moduleGroupCount");
//        $sql.addJoin(plainSelect, "left join", "tenant_modulegroup", "b", $sql.exp("b.tenant_uuid", "=", "a.uuid"));
//        $sql.addWhereExpression(plainSelect, $sql.exp("a.is_active", "=", 1));
//        $sql.addGroupBy(plainSelect, "a.name");
//        $sql.addOrderBy(plainSelect, "a.name", "desc");
//        $sql.setLimit(plainSelect, 3, 20);
//        System.out.println("plainSelect = " + plainSelect);
    }

    private static void testSql(List<String> methodNameList) {
        String sql = "SELECT count(a.name) AS countName, group_concat(a.name) AS tenantName, b.module_group, count(c.module_id) AS countModuleId FROM tenant a LEFT JOIN tenant_modulegroup b ON b.tenant_uuid = a.uuid LEFT JOIN tenant_module c ON c.tenant_uuid = a.uuid WHERE a.is_active = 1 AND a.name IS NOT NULL AND b.module_group IS NOT NULL AND c.module_id IS NOT NULL GROUP BY a.name, b.module_group ORDER BY a.name, b.module_group DESC LIMIT 2, 3";
//        public static PlainSelect addSql(SqlVo)
        {
            SqlVo sqlVo = new SqlVo();
            sqlVo.withFromTable($sql.join("", "tenant", "a")
                    .withAddWhereExpression($sql.exp("a.is_active", "=", 1))
                    .withAddSelectColumn(new ColumnVo($sql.fun("count", "a.name"), "countName"))
            );
            sqlVo.withAddJoin($sql.join("left join", "tenant_modulegroup", "b")
                    .withOn($sql.exp("b.tenant_uuid", "=", "a.uuid"))
                    .withAddWhereExpression($sql.exp("b.module_group", "is not null"))
                    .withAddGroupBy(new GroupByVo("b.module_group").withSort(10))
                    .withAddOrderBy(new OrderByVo("b.module_group", "desc").withSort(10))
                    .withAddSelectColumn(new ColumnVo("b.module_group"))
            );
            sqlVo.withAddJoin($sql.join("left join", "tenant_module", "c")
                    .withOn($sql.exp("c.tenant_uuid", "=", "a.uuid"))
                    .withAddWhereExpression($sql.exp("c.module_id", "is not null"))
                    .withAddSelectColumn(new ColumnVo($sql.fun("count", "c.module_id"), "countModuleId")));
            sqlVo.withAddWhereExpression($sql.exp("a.name", "is not null"));
            sqlVo.withAddGroupBy(new GroupByVo("a.name"));
            sqlVo.withAddOrderBy(new OrderByVo("a.name", "asc"));
            sqlVo.withAddSelectColumn(new ColumnVo($sql.fun("group_concat", "a.name"), "tenantName"));
            sqlVo.withLimit(new LimitVo(2, 3));
            PlainSelect plainSelect = $sql.addSql(sqlVo);
            Assert.isTrue(Objects.equals(plainSelect.toString(), sql), "测试失败");
            methodNameList.remove("public static PlainSelect addSql(SqlVo)");
        }
//        public static void addSql(PlainSelect, SqlVo)
        {
            SqlVo sqlVo = new SqlVo();
            PlainSelect plainSelect = $sql.from("tenant", "a");
            sqlVo.withAddWhereExpression($sql.exp("a.is_active", "=", 1));
            sqlVo.withAddSelectColumn(new ColumnVo($sql.fun("count", "a.name"), "countName"));
            sqlVo.withAddJoin($sql.join("left join", "tenant_modulegroup", "b")
                    .withOn($sql.exp("b.tenant_uuid", "=", "a.uuid"))
                    .withAddWhereExpression($sql.exp("b.module_group", "is not null"))
                    .withAddGroupBy(new GroupByVo("b.module_group").withSort(10))
                    .withAddOrderBy(new OrderByVo("b.module_group", "desc").withSort(10))
                    .withAddSelectColumn(new ColumnVo("b.module_group"))
            );
            sqlVo.withAddJoin($sql.join("left join", "tenant_module", "c")
                    .withOn($sql.exp("c.tenant_uuid", "=", "a.uuid"))
                    .withAddWhereExpression($sql.exp("c.module_id", "is not null"))
                    .withAddSelectColumn(new ColumnVo($sql.fun("count", "c.module_id"), "countModuleId")));
            sqlVo.withAddWhereExpression($sql.exp("a.name", "is not null"));
            sqlVo.withAddGroupBy(new GroupByVo("a.name"));
            sqlVo.withAddOrderBy(new OrderByVo("a.name", "asc"));
            sqlVo.withAddSelectColumn(new ColumnVo($sql.fun("group_concat", "a.name"), "tenantName"));
            sqlVo.withLimit(new LimitVo(2, 3));
            $sql.addSql(plainSelect, sqlVo);
            Assert.isTrue(Objects.equals(plainSelect.toString(), sql), "测试失败");
            methodNameList.remove("public static void addSql(PlainSelect, SqlVo)");
        }
    }
    private static void testForm(List<String> methodNameList) {
        // public static PlainSelect from(String)
        Assert.isTrue(Objects.equals($sql.from("tenant").toString(), "SELECT  FROM tenant"), "测试失败");
        Assert.isTrue(Objects.equals($sql.from("neatlogic.tenant").toString(), "SELECT  FROM neatlogic.tenant"), "测试失败");
        Assert.isTrue(Objects.equals($sql.from("tenant a").toString(), "SELECT  FROM tenant a"), "测试失败");
        Assert.isTrue(Objects.equals($sql.from("neatlogic.tenant a").toString(), "SELECT  FROM neatlogic.tenant a"), "测试失败");

        methodNameList.remove("public static PlainSelect from(String)");

        // public static PlainSelect from(String, String)
        Assert.isTrue(Objects.equals($sql.from("tenant", "a").toString(), "SELECT  FROM tenant a"), "测试失败");
        Assert.isTrue(Objects.equals($sql.from("neatlogic.tenant", "a").toString(), "SELECT  FROM neatlogic.tenant a"), "测试失败");

        methodNameList.remove("public static PlainSelect from(String, String)");

        // public static PlainSelect from(String, String, String)
        Assert.isTrue(Objects.equals($sql.from("neatlogic", "tenant", "a").toString(), "SELECT  FROM neatlogic.tenant a"), "测试失败");
        methodNameList.remove("public static PlainSelect from(String, String, String)");

        PlainSelect plainSelect = new PlainSelect();
        // public static void from(PlainSelect, String)
        $sql.from(plainSelect, "tenant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant"), "测试失败");

        $sql.from(plainSelect, "neatlogic.tenant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM neatlogic.tenant"), "测试失败");

        $sql.from(plainSelect, "tenant a");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant a"), "测试失败");

        $sql.from(plainSelect, "neatlogic.tenant a");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM neatlogic.tenant a"), "测试失败");

        methodNameList.remove("public static void from(PlainSelect, String)");

        // public static void from(PlainSelect, String, String)
        $sql.from(plainSelect, "tenant", "a");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant a"), "测试失败");

        $sql.from(plainSelect, "neatlogic.tenant", "a");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM neatlogic.tenant a"), "测试失败");

        methodNameList.remove("public static void from(PlainSelect, String, String)");

        // public static void from(PlainSelect, String, String, String)
        $sql.from(plainSelect, "neatlogic", "tenant", "a");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM neatlogic.tenant a"), "测试失败");

        methodNameList.remove("public static void from(PlainSelect, String, String, String)");
    }

    private static void testSetDistinct(List<String> methodNameList) {
//        public static void setDistinct(PlainSelect, boolean)
        PlainSelect plainSelect = $sql.from("tenant");
        $sql.setDistinct(plainSelect, true);
        $sql.setSelectColumn(plainSelect, "name");
        System.out.println("plainSelect = " + plainSelect);
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT DISTINCT name FROM tenant"), "测试失败");

        $sql.setDistinct(plainSelect, false);
        $sql.setSelectColumn(plainSelect, "uuid");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid FROM tenant"), "测试失败");
        methodNameList.remove("public static void setDistinct(PlainSelect, boolean)");
    }
    private static void testSetSelectColumn(List<String> methodNameList) {
        PlainSelect plainSelect = $sql.from("tenant");
//        public static void setSelectColumn(PlainSelect, String)
        $sql.setSelectColumn(plainSelect, "name");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT name FROM tenant"), "测试失败");

        methodNameList.remove("public static void setSelectColumn(PlainSelect, String)");

//        public static void setSelectColumn(PlainSelect, String, String)
        $sql.setSelectColumn(plainSelect, "name", "tenantName");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT name AS tenantName FROM tenant"), "测试失败");

        methodNameList.remove("public static void setSelectColumn(PlainSelect, String, String)");

//        public static void setSelectColumn(PlainSelect, ValueVo)
        $sql.setSelectColumn(plainSelect, $sql.value("abc"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 'abc' FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.value(10));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 10 FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.value(100L));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 100 FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.value(0.25));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 0.25 FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.value(Arrays.asList("bcd", "cde")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 'bcd', 'cde' FROM tenant"), "测试失败");

        methodNameList.remove("public static void setSelectColumn(PlainSelect, ValueVo)");

//        public static void setSelectColumn(PlainSelect, ValueVo, String)
        $sql.setSelectColumn(plainSelect, $sql.value("abc"), "constant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 'abc' AS constant FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.value(10), "constant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 10 AS constant FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.value(100L), "constant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 100 AS constant FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.value(0.25), "constant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 0.25 AS constant FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.value(Arrays.asList("bcd", "cde")), "constant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 'bcd' AS constant, 'cde' AS constant FROM tenant"), "测试失败");
        methodNameList.remove("public static void setSelectColumn(PlainSelect, ValueVo, String)");
//        public static void setSelectColumn(PlainSelect, FunctionVo)
        $sql.setSelectColumn(plainSelect, $sql.fun("count", "name"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT count(name) FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("count", $sql.value(1)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT count(1) FROM tenant"), "测试失败");
        methodNameList.remove("public static void setSelectColumn(PlainSelect, FunctionVo)");
//        public static void setSelectColumn(PlainSelect, FunctionVo, String)
        $sql.setSelectColumn(plainSelect, $sql.fun("count", "name"), "countName");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT count(name) AS countName FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("count", $sql.value(1)), "count");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT count(1) AS count FROM tenant"), "测试失败");
        methodNameList.remove("public static void setSelectColumn(PlainSelect, FunctionVo, String)");
    }

    private static void testAddSelectColumn(List<String> methodNameList) {
//        public static void addSelectColumn(PlainSelect, String)
        PlainSelect plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, "name");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, name FROM tenant"), "测试失败");

        methodNameList.remove("public static void addSelectColumn(PlainSelect, String)");
//        public static void addSelectColumn(PlainSelect, String, String)
        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, "name", "tenantName");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, name AS tenantName FROM tenant"), "测试失败");

        methodNameList.remove("public static void addSelectColumn(PlainSelect, String, String)");
//        public static void addSelectColumn(PlainSelect, ValueVo)
        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.value("abc"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, 'abc' FROM tenant"), "测试失败");


        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.value(10));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, 10 FROM tenant"), "测试失败");


        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.value(100L));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, 100 FROM tenant"), "测试失败");


        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.value(0.25));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, 0.25 FROM tenant"), "测试失败");


        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.value(Arrays.asList("bcd", "cde")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, 'bcd', 'cde' FROM tenant"), "测试失败");

        methodNameList.remove("public static void addSelectColumn(PlainSelect, ValueVo)");
//        public static void addSelectColumn(PlainSelect, ValueVo, String)
        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.value("abc"), "constant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, 'abc' AS constant FROM tenant"), "测试失败");

        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.value(10), "constant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, 10 AS constant FROM tenant"), "测试失败");

        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.value(100L), "constant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, 100 AS constant FROM tenant"), "测试失败");

        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.value(0.25), "constant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, 0.25 AS constant FROM tenant"), "测试失败");

        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.value(Arrays.asList("bcd", "cde")), "constant");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, 'bcd' AS constant, 'cde' AS constant FROM tenant"), "测试失败");
        methodNameList.remove("public static void addSelectColumn(PlainSelect, ValueVo, String)");

//        public static void addSelectColumn(PlainSelect, FunctionVo)
        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.fun("count", "name"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, count(name) FROM tenant"), "测试失败");

        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.fun("count", $sql.value(1)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, count(1) FROM tenant"), "测试失败");
        methodNameList.remove("public static void addSelectColumn(PlainSelect, FunctionVo)");
//        public static void addSelectColumn(PlainSelect, FunctionVo, String)
        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.fun("count", "name"), "countName");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, count(name) AS countName FROM tenant"), "测试失败");

        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.fun("count", $sql.value(1)), "count");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, count(1) AS count FROM tenant"), "测试失败");
        methodNameList.remove("public static void addSelectColumn(PlainSelect, FunctionVo, String)");
    }

    private static void testAddJoin(List<String> methodNameList) {
//        public static void addJoin(PlainSelect, String, String, String, String, ExpressionVo)
        PlainSelect plainSelect = $sql.from("tenant", "a");
        $sql.addJoin(plainSelect, "left join", "neatlogic", "tenant_modulegroup", "b", $sql.exp("b.tenant_uuid", "=", "a.uuid"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant a LEFT JOIN neatlogic.tenant_modulegroup b ON b.tenant_uuid = a.uuid"), "测试失败");
        methodNameList.remove("public static void addJoin(PlainSelect, String, String, String, String, ExpressionVo)");
//        public static JoinVo join(String, String, String, String, ExpressionVo)
        plainSelect = $sql.from("tenant", "a");
        $sql.addJoin(plainSelect, $sql.join("left join", "neatlogic", "tenant_modulegroup", "b", $sql.exp("b.tenant_uuid", "=", "a.uuid")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant a LEFT JOIN neatlogic.tenant_modulegroup b ON b.tenant_uuid = a.uuid"), "测试失败");
        methodNameList.remove("public static JoinVo join(String, String, String, String, ExpressionVo)");

//        public static void addJoin(PlainSelect, String, String, String, ExpressionVo)
        plainSelect = $sql.from("tenant", "a");
        $sql.addJoin(plainSelect, "left join", "tenant_modulegroup", "b", $sql.exp("b.tenant_uuid", "=", "a.uuid"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant a LEFT JOIN tenant_modulegroup b ON b.tenant_uuid = a.uuid"), "测试失败");
        methodNameList.remove("public static void addJoin(PlainSelect, String, String, String, ExpressionVo)");
//        public static JoinVo join(String, String, String, ExpressionVo)
//        public static void addJoin(PlainSelect, JoinVo)
        plainSelect = $sql.from("tenant", "a");
        $sql.addJoin(plainSelect, $sql.join("left join", "tenant_modulegroup", "b", $sql.exp("b.tenant_uuid", "=", "a.uuid")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant a LEFT JOIN tenant_modulegroup b ON b.tenant_uuid = a.uuid"), "测试失败");
        methodNameList.remove("public static JoinVo join(String, String, String, ExpressionVo)");
        methodNameList.remove("public static void addJoin(PlainSelect, JoinVo)");
//        public static JoinVo join(String, String, String)
//        public static void addJoinList(PlainSelect, List)
        plainSelect = $sql.from("tenant", "a");
        List<JoinVo> joinList = new ArrayList<>();
        joinList.add($sql.join("left join", "tenant_modulegroup", "b"));
        joinList.add($sql.join("left join", "tenant_module", "c"));
        $sql.addJoinList(plainSelect, joinList);
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant a LEFT JOIN tenant_modulegroup b LEFT JOIN tenant_module c"), "测试失败");
        methodNameList.remove("public static JoinVo join(String, String, String)");
        methodNameList.remove("public static void addJoinList(PlainSelect, List)");
    }

    private static void testAddWhereExpressionList(List<String> methodNameList) {
//        public static void addWhereExpression(PlainSelect, ExpressionVo)
        PlainSelect plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("name", "=", $sql.value("demo")));
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "=", $sql.value(1)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE name = 'demo' AND is_active = 1"), "测试失败");
        methodNameList.remove("public static void addWhereExpression(PlainSelect, ExpressionVo)");
//        public static void addWhereExpression(PlainSelect, String, ExpressionVo)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("name", "=", $sql.value("demo")));
        $sql.addWhereExpression(plainSelect, "or", $sql.exp("is_active", "=", $sql.value(1)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE name = 'demo' OR is_active = 1"), "测试失败");
        methodNameList.remove("public static void addWhereExpression(PlainSelect, String, ExpressionVo)");

//        public static void addWhereExpressionList(PlainSelect, List)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("name", "=", $sql.value("demo")));
        List<ExpressionVo> expressionList = new ArrayList<>();
        expressionList.add($sql.exp("is_active", "=", $sql.value(1)));
        expressionList.add($sql.exp("status", "=", $sql.value("built")));
        $sql.addWhereExpressionList(plainSelect, expressionList);
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE name = 'demo' AND is_active = 1 AND status = 'built'"), "测试失败");
        methodNameList.remove("public static void addWhereExpressionList(PlainSelect, List)");

//        public static void addWhereExpressionList(PlainSelect, String, List)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("name", "=", $sql.value("demo")));
        expressionList = new ArrayList<>();
        expressionList.add($sql.exp("is_active", "=", $sql.value(1)));
        expressionList.add($sql.exp("status", "=", $sql.value("built")));
        $sql.addWhereExpressionList(plainSelect, "or", expressionList);
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE name = 'demo' OR is_active = 1 OR status = 'built'"), "测试失败");
        methodNameList.remove("public static void addWhereExpressionList(PlainSelect, String, List)");
    }

    private static void testAddGroupBy(List<String> methodNameList) {
//        public static void addGroupBy(PlainSelect, String)
        PlainSelect plainSelect = $sql.from("tenant");
        $sql.addGroupBy(plainSelect, "name");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant GROUP BY name"), "测试失败");
        $sql.addGroupBy(plainSelect, "status");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant GROUP BY name, status"), "测试失败");
        methodNameList.remove("public static void addGroupBy(PlainSelect, String)");
//        public static void addGroupBy(PlainSelect, Column)
        plainSelect = $sql.from("tenant");
        $sql.addGroupBy(plainSelect, new Column("name"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant GROUP BY name"), "测试失败");
        $sql.addGroupBy(plainSelect, new Column("status"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant GROUP BY name, status"), "测试失败");
        methodNameList.remove("public static void addGroupBy(PlainSelect, Column)");
    }

    private static void testAddOrderBy(List<String> methodNameList) {
//        public static void addOrderBy(PlainSelect, String)
        PlainSelect plainSelect = $sql.from("tenant");
        $sql.addOrderBy(plainSelect, "name");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant ORDER BY name"), "测试失败");
        $sql.addOrderBy(plainSelect, "status");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant ORDER BY name, status"), "测试失败");
        methodNameList.remove("public static void addOrderBy(PlainSelect, String)");
//        public static void addOrderBy(PlainSelect, String, String)
        plainSelect = $sql.from("tenant");
        $sql.addOrderBy(plainSelect, "name", "asc");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant ORDER BY name"), "测试失败");
        $sql.addOrderBy(plainSelect, "status", "desc");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant ORDER BY name, status DESC"), "测试失败");
        methodNameList.remove("public static void addOrderBy(PlainSelect, String, String)");
//        public static void addOrderBy(PlainSelect, FunctionVo)
        plainSelect = $sql.from("tenant", "a");
        $sql.addSelectColumn(plainSelect, "a.name");
        $sql.addSelectColumn(plainSelect, $sql.fun("count", "a.name"));
        $sql.addGroupBy(plainSelect, "a.name");
        $sql.addOrderBy(plainSelect, $sql.fun("count", "a.name"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT a.name, count(a.name) FROM tenant a GROUP BY a.name ORDER BY count(a.name)"), "测试失败");
        methodNameList.remove("public static void addOrderBy(PlainSelect, FunctionVo)");
//        public static void addOrderBy(PlainSelect, FunctionVo, String)
        plainSelect = $sql.from("tenant", "a");
        $sql.addSelectColumn(plainSelect, "a.name");
        $sql.addSelectColumn(plainSelect, $sql.fun("count", "a.name"));
        $sql.addGroupBy(plainSelect, "a.name");
        $sql.addOrderBy(plainSelect, $sql.fun("count", "a.name"), "desc");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT a.name, count(a.name) FROM tenant a GROUP BY a.name ORDER BY count(a.name) DESC"), "测试失败");
        methodNameList.remove("public static void addOrderBy(PlainSelect, FunctionVo, String)");

    }

    private static void testSetLimit(List<String> methodNameList) {
        PlainSelect plainSelect = $sql.from("tenant");
        $sql.addSelectColumn(plainSelect, "*");
//        public static void setLimit(PlainSelect, int)
        $sql.setLimit(plainSelect, 20);
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT * FROM tenant LIMIT 0, 20"), "测试失败");
        methodNameList.remove("public static void setLimit(PlainSelect, int)");
//        public static void setLimit(PlainSelect, int, int)
        $sql.setLimit(plainSelect, 10, 20);
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT * FROM tenant LIMIT 10, 20"), "测试失败");
        methodNameList.remove("public static void setLimit(PlainSelect, int, int)");

    }

    private static void testValue(List<String> methodNameList) {
        PlainSelect plainSelect = $sql.from("tenant");
//        public static ValueVo value(String)
        $sql.setSelectColumn(plainSelect, $sql.value("abc"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 'abc' FROM tenant"), "测试失败");
        methodNameList.remove("public static ValueVo value(String)");
//        public static ValueVo value(Integer)
        $sql.setSelectColumn(plainSelect, $sql.value(10));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 10 FROM tenant"), "测试失败");
        methodNameList.remove("public static ValueVo value(Integer)");
//        public static ValueVo value(Long)
        $sql.setSelectColumn(plainSelect, $sql.value(100L));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 100 FROM tenant"), "测试失败");
        methodNameList.remove("public static ValueVo value(Long)");
//        public static ValueVo value(Double)
        $sql.setSelectColumn(plainSelect, $sql.value(0.25));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 0.25 FROM tenant"), "测试失败");
        methodNameList.remove("public static ValueVo value(Double)");
//        public static ValueVo value(List)
        $sql.setSelectColumn(plainSelect, $sql.value(Arrays.asList("bcd", "cde")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT 'bcd', 'cde' FROM tenant"), "测试失败");
        methodNameList.remove("public static ValueVo value(List)");
    }

    private static void testExp(List<String> methodNameList) {
//        public static ExpressionVo exp(String, String)
        PlainSelect plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("name", "is null"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE name IS NULL"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, String)");
//        public static ExpressionVo exp(String, String, String)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("name", "=", "uuid"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE name = uuid"), "测试失败");
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("name", "=", "'demo'"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE name = 'demo'"), "测试失败");
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "=", "1"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE is_active = 1"), "测试失败");
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "=", "0.99"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE is_active = 0.99"), "测试失败");
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "in", "(0, 1)"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE is_active IN (0, 1)"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, String, String)");
//        public static ExpressionVo exp(String, String, FunctionVo)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("visit_time", ">=", $sql.fun("STR_TO_DATE", "'2023-11-08 09:57:01'", "'%Y-%m-%d %H:%i:%s'")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE visit_time >= STR_TO_DATE('2023-11-08 09:57:01', '%Y-%m-%d %H:%i:%s')"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, String, FunctionVo)");
//        public static ExpressionVo exp(String, String, Integer)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "=", 0));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE is_active = 0"), "测试失败");
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "in", 0));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE is_active IN (0)"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, String, Integer)");
//        public static ExpressionVo exp(String, String, Long)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "=", 1L));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE is_active = 1"), "测试失败");
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "in", 1L));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE is_active IN (1)"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, String, Long)");
//        public static ExpressionVo exp(String, String, Double)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "=", 0.99));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE is_active = 0.99"), "测试失败");
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "in", 0.99));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE is_active IN (0.99)"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, String, Double)");
//        public static ExpressionVo exp(String, String, List)
        plainSelect = $sql.from("tenant");
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        $sql.addWhereExpression(plainSelect, $sql.exp("is_active", "in", list));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE is_active IN (0, 1)"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, String, List)");
//        public static ExpressionVo exp(String, String, Date)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("fcd", "=", new Date(1752993694062L)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE fcd = '2025-07-20 14:41:34'"), "测试失败");
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("fcd", "in", new Date(1752993694062L)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE fcd IN ('2025-07-20 14:41:34')"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, String, Date)");
//        public static ExpressionVo exp(Integer, String, Integer)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp(0, "=", 1));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE 0 = 1"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(Integer, String, Integer)");
//        public static ExpressionVo exp(Integer, String, Long)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp(0, "=", 2L));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE 0 = 2"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(Integer, String, Long)");
//        public static ExpressionVo exp(Integer, String, Double)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp(0, "=", 0.99));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE 0 = 0.99"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(Integer, String, Double)");
//        public static ExpressionVo exp(Long, String, Integer)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp(3L, "=", 0));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE 3 = 0"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(Long, String, Integer)");
//        public static ExpressionVo exp(Long, String, Long)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp(3L, "=", 4L));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE 3 = 4"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(Long, String, Long)");
//        public static ExpressionVo exp(Long, String, Double)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp(3L, "=", 1.68));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE 3 = 1.68"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(Long, String, Double)");
//        public static ExpressionVo exp(Double, String, Integer)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp(1.68, "=", 1));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE 1.68 = 1"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(Double, String, Integer)");
//        public static ExpressionVo exp(Double, String, Long)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp(1.68, "=", 5L));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE 1.68 = 5"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(Double, String, Long)");
//        public static ExpressionVo exp(Double, String, Double)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp(1.68, "=", 1.68));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE 1.68 = 1.68"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(Double, String, Double)");
//        public static ExpressionVo exp(String, String, ValueVo)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("name", "=", $sql.value("demo")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE name = 'demo'"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, String, ValueVo)");
//        public static ExpressionVo exp(ValueVo, String, ValueVo)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp($sql.value(1), "=", $sql.value(0)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE 1 = 0"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(ValueVo, String, ValueVo)");
//        public static ExpressionVo exp(ExpressionVo, String, ExpressionVo)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp($sql.exp("uuid", "=", "name"), "and", $sql.exp("name", "=", "uuid")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE uuid = name AND name = uuid"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(ExpressionVo, String, ExpressionVo)");
//        public static ExpressionVo exp(String, ExpressionVo, String, ExpressionVo, String)
        plainSelect = $sql.from("tenant");
        $sql.addWhereExpression(plainSelect, $sql.exp("(", $sql.exp("uuid", "=", "name"), "and", $sql.exp("name", "=", "uuid"), ")"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE (uuid = name AND name = uuid)"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, ExpressionVo, String, ExpressionVo, String)");
//        public static ExpressionVo exp(String, ExpressionVo, String)
        plainSelect = $sql.from("tenant");
        ExpressionVo orExp = $sql.exp($sql.exp("uuid", "=", "name"), "or", $sql.exp("name", "=", "uuid"));
        $sql.addWhereExpression(plainSelect, $sql.exp("(", orExp, ")"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT  FROM tenant WHERE (uuid = name OR name = uuid)"), "测试失败");
        methodNameList.remove("public static ExpressionVo exp(String, ExpressionVo, String)");
    }

    private static void testFun(List<String> methodNameList) {
        PlainSelect plainSelect = new PlainSelect();
//        public static transient FunctionVo fun(String, Object[])
        $sql.setSelectColumn(plainSelect, $sql.fun("count", $sql.value(1)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT count(1)"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("count", 1));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT count(1)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("CONCAT", $sql.value("Hello"), $sql.value(" "), $sql.value("MySQL")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT CONCAT('Hello', ' ', 'MySQL')"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("CONCAT", "'Hello'", "' '", "'MySQL'"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT CONCAT('Hello', ' ', 'MySQL')"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("SUBSTRING", $sql.value("MySQL"), $sql.value(2), $sql.value(3)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT SUBSTRING('MySQL', 2, 3)"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("SUBSTRING", "'MySQL'", 2, 3));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT SUBSTRING('MySQL', 2, 3)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("SUBSTRING", $sql.value("MySQL"), $sql.value(-3)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT SUBSTRING('MySQL', -3)"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("SUBSTRING", "'MySQL'", -3));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT SUBSTRING('MySQL', -3)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("LENGTH", $sql.value("MySQL")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT LENGTH('MySQL')"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("LENGTH", "'MySQL'"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT LENGTH('MySQL')"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("CHAR_LENGTH", $sql.value("你好")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT CHAR_LENGTH('你好')"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("CHAR_LENGTH", "'你好'"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT CHAR_LENGTH('你好')"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("UPPER", $sql.value("mysql")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT UPPER('mysql')"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("UPPER", "'mysql'"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT UPPER('mysql')"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("LOWER", $sql.value("MySQL")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT LOWER('MySQL')"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("LOWER", "'MySQL'"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT LOWER('MySQL')"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("TRIM", $sql.value("  MySQL   ")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT TRIM('  MySQL   ')"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("TRIM", "'  MySQL   '"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT TRIM('  MySQL   ')"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("TRIM", "BOTH 'x' FROM 'xxMySQLxx'"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT TRIM(BOTH 'x' FROM 'xxMySQLxx')"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("ROUND", $sql.value(3.14159), $sql.value(2)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT ROUND(3.14159, 2)"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("ROUND", 3.14159, 2));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT ROUND(3.14159, 2)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("ROUND", $sql.value(123.456), $sql.value(-1)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT ROUND(123.456, -1)"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("ROUND", 123.456, -1));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT ROUND(123.456, -1)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("FLOOR", $sql.value(3.7)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT FLOOR(3.7)"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("FLOOR", 3.7));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT FLOOR(3.7)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("CEIL", $sql.value(3.2)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT CEIL(3.2)"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("CEIL", 3.2));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT CEIL(3.2)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("ABS", $sql.value(-10)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT ABS(-10)"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("ABS", -10));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT ABS(-10)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("MOD", $sql.value(10), $sql.value(3)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT MOD(10, 3)"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("MOD", 10, 3));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT MOD(10, 3)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("POWER", $sql.value(2), $sql.value(3)));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT POWER(2, 3)"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("POWER", 2, 3));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT POWER(2, 3)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("NOW"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT NOW()"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("CURDATE"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT CURDATE()"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("CURTIME"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT CURTIME()"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("DATE_FORMAT", $sql.fun("NOW"), $sql.value("%Y年%m月%d日 %H:%i:%s")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT DATE_FORMAT(NOW(), '%Y年%m月%d日 %H:%i:%s')"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("DATE_FORMAT", $sql.fun("NOW"), "'%Y年%m月%d日 %H:%i:%s'"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT DATE_FORMAT(NOW(), '%Y年%m月%d日 %H:%i:%s')"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("DATEDIFF", $sql.value("2023-12-31"), $sql.value("2023-01-01")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT DATEDIFF('2023-12-31', '2023-01-01')"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("DATEDIFF", "'2023-12-31'", "'2023-01-01'"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT DATEDIFF('2023-12-31', '2023-01-01')"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("DATE_ADD", $sql.fun("NOW"), "INTERVAL 1 MONTH"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT DATE_ADD(NOW(), INTERVAL 1 MONTH)"), "测试失败");

        $sql.setSelectColumn(plainSelect, $sql.fun("DATE_SUB", $sql.fun("NOW"), "INTERVAL 1 WEEK"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT DATE_SUB(NOW(), INTERVAL 1 WEEK)"), "测试失败");

        plainSelect = $sql.from("tenant");
        $sql.setSelectColumn(plainSelect, $sql.fun("IF", $sql.exp("name", "=", $sql.value("demo")), $sql.value("及格"), $sql.value("不及格")));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT IF(name = 'demo', '及格', '不及格') FROM tenant"), "测试失败");
        $sql.setSelectColumn(plainSelect, $sql.fun("IF", $sql.exp("name", "=", "'demo'"), "'及格'", "'不及格'"));
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT IF(name = 'demo', '及格', '不及格') FROM tenant"), "测试失败");

        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, $sql.fun("GROUP_CONCAT", "name SEPARATOR ', '"));
        $sql.addGroupBy(plainSelect, "uuid");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, GROUP_CONCAT(name SEPARATOR ', ') FROM tenant GROUP BY uuid"), "测试失败");

        $sql.setSelectColumn(plainSelect, "uuid");
        $sql.addSelectColumn(plainSelect, "CASE WHEN name >= 90 THEN '优秀' WHEN name >= 80 THEN '良好' WHEN name >= 60 THEN '及格' ELSE '不及格' END", "grade");
        Assert.isTrue(Objects.equals(plainSelect.toString(), "SELECT uuid, CASE WHEN name >= 90 THEN '优秀' WHEN name >= 80 THEN '良好' WHEN name >= 60 THEN '及格' ELSE '不及格' END AS grade FROM tenant GROUP BY uuid"), "测试失败");
        methodNameList.remove("public static transient FunctionVo fun(String, Object[])");
    }

    private static List<String> getMethodNameList(Class<?> clazz) {
        List<String> resultList = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            String modifierString = Modifier.toString(method.getModifiers());
            if (modifierString.startsWith("public") && modifierString.contains("static")) {
                List<String> paramTypeNameList = new ArrayList<>();
                Class<?>[] paramTypes = method.getParameterTypes();
                for (Class<?> paramType : paramTypes) {
                    paramTypeNameList.add(paramType.getSimpleName());
                }
                String result = modifierString + " " + method.getReturnType().getSimpleName() + " " + method.getName() + "(" + String.join(", ", paramTypeNameList) + ")";
                resultList.add(result);
            }
        }
        return resultList;
    }
}

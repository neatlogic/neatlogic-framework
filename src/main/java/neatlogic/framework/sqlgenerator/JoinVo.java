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

import org.apache.commons.lang3.StringUtils;

public class JoinVo {
    // join类型，LEFT JOIN, JOIN
    private final String operationSymbol;
    private final String schemaName;
    private final String tableName;
    private final String alias;
    private ExpressionVo on;

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

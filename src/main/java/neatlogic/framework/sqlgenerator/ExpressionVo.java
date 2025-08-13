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

import java.util.Objects;

public class ExpressionVo {

    private String leftParenthesis;
    private String rightParenthesis;
    private String leftColumn;
    private String rightColumn;
    private ValueVo leftValueExpression;
    private ValueVo rightValueExpression;
    private FunctionVo rightFunctionVo;
    private ExpressionVo leftExpressionVo;
    private ExpressionVo rightExpressionVo;
    private final String operationSymbol;
    private final String type;

    ExpressionVo(String leftParenthesis, ExpressionVo leftExpressionVo, String rightParenthesis) {
        this(leftParenthesis, leftExpressionVo, "and", null, rightParenthesis);
    }

    ExpressionVo(ExpressionVo leftExpressionVo, String operationType, ExpressionVo rightExpressionVo) {
        this(null, leftExpressionVo, operationType, rightExpressionVo, null);
    }

    ExpressionVo(String leftParenthesis, ExpressionVo leftExpressionVo, String operationSymbol, ExpressionVo rightExpressionVo, String rightParenthesis) {
        this.leftParenthesis = leftParenthesis;
        this.leftExpressionVo = leftExpressionVo;
        this.operationSymbol = operationSymbol;
        this.rightExpressionVo = rightExpressionVo;
        this.rightParenthesis = rightParenthesis;
        this.type = $sql.BOOLEAN_OPERATION;
    }

    ExpressionVo(String leftColumn, String operationSymbol, String rightColumn) {
        this.leftColumn = leftColumn;
        this.operationSymbol = operationSymbol;
        this.rightColumn = rightColumn;
        this.type = $sql.COMPARATIVE_OPERATION;
    }

    ExpressionVo(String leftColumn, String operationSymbol) {
        this(leftColumn, operationSymbol, new ValueVo(""));
    }

    ExpressionVo(String leftColumn, String operationSymbol, FunctionVo rightFunctionVo) {
        this.leftColumn = leftColumn;
        this.operationSymbol = operationSymbol;
        this.rightFunctionVo = rightFunctionVo;
        this.type = $sql.COMPARATIVE_OPERATION;
    }

    ExpressionVo(String leftColumn, String operationSymbol, ValueVo rightValueExpression) {
        this.leftColumn = leftColumn;
        this.operationSymbol = operationSymbol;
        this.rightValueExpression = rightValueExpression;
        this.type = $sql.COMPARATIVE_OPERATION;
    }

    ExpressionVo(ValueVo leftValueExpression, String operationSymbol, ValueVo rightValueExpression) {
        this.leftValueExpression = leftValueExpression;
        this.operationSymbol = operationSymbol;
        this.rightValueExpression = rightValueExpression;
        this.type = $sql.COMPARATIVE_OPERATION;
    }

    public String getLeftParenthesis() {
        return leftParenthesis;
    }

    public ExpressionVo getLeftExpression() {
        return leftExpressionVo;
    }

    public ExpressionVo getRightExpression() {
        return rightExpressionVo;
    }

    public String getRightParenthesis() {
        return rightParenthesis;
    }

    public String getLeftColumn() {
        return leftColumn;
    }

    public String getRightColumn() {
        return rightColumn;
    }

    public ValueVo getLeftValueExpression() {
        return leftValueExpression;
    }

    public ValueVo getRightValueExpression() {
        return rightValueExpression;
    }

    public FunctionVo getRightFunctionVo() {
        return rightFunctionVo;
    }

    public String getOperationSymbol() {
        return operationSymbol;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        String result = StringUtils.EMPTY;
        if (Objects.equals(type, $sql.BOOLEAN_OPERATION)) {
            if (this.leftParenthesis != null) {
                result += this.leftParenthesis;
            }
            if (this.leftExpressionVo != null) {
                result += this.leftExpressionVo.toString();
            }
            if (this.operationSymbol != null) {
                result += " " + this.operationSymbol + " ";
            }
            if (this.rightExpressionVo != null) {
                result += this.rightExpressionVo.toString();
            }
            if (this.rightParenthesis != null) {
                result += this.rightParenthesis;
            }
        } else if (Objects.equals(type, $sql.COMPARATIVE_OPERATION)) {
            if (this.leftColumn != null) {
                result += this.leftColumn;
            }
            if (this.leftValueExpression != null) {
                result += this.leftValueExpression.toString();
            }
            if (this.operationSymbol != null) {
                result += " " + this.operationSymbol + " ";
            }
            if (this.rightColumn != null) {
                result += this.rightColumn;
            }
            if (this.rightValueExpression != null) {
                result += this.rightValueExpression.toString();
            }
        }
        return result;
    }
}

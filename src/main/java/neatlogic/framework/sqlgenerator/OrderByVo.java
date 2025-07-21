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

public class OrderByVo {

    private String columnName;
    private FunctionVo function;
    private String asc;
    private int sort;;

    public OrderByVo(String columnName) {
        this.columnName = columnName;
    }

    public OrderByVo(String columnName, String asc) {
        this.columnName = columnName;
        this.asc = asc;
    }

    public OrderByVo(FunctionVo function) {
        this.function = function;
    }

    public OrderByVo(FunctionVo function, String asc) {
        this.function = function;
        this.asc = asc;
    }

    public String getColumnName() {
        return columnName;
    }

    public FunctionVo getFunction() {
        return function;
    }

    public String getAsc() {
        return asc;
    }

    public int getSort() {
        return sort;
    }

    public OrderByVo withSort(int sort) {
        this.sort = sort;
        return this;
    }
}

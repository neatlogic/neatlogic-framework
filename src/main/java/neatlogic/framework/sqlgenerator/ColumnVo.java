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

public class ColumnVo {
    private String name;
    private FunctionVo function;
    private String alias;

    public ColumnVo(String name) {
        this.name = name;
    }

    public ColumnVo(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public ColumnVo(FunctionVo function) {
        this.function = function;
    }

    public ColumnVo(FunctionVo function, String alias) {
        this.function = function;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public FunctionVo getFunction() {
        return function;
    }

    public String getAlias() {
        return alias;
    }

    public ColumnVo withAlias(String alias) {
        this.alias = alias;
        return this;
    }
}

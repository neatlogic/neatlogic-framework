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

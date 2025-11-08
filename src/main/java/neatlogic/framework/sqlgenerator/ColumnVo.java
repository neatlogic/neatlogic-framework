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

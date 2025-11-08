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

package neatlogic.framework.importexport.dto;

import java.io.Serializable;

public class ImportExportBaseInfoVo implements Serializable {

    private static final long serialVersionUID = -5142624297760626821L;
    private String type;
    private Object primaryKey;
    private String name;

    public ImportExportBaseInfoVo() {
    }

    public ImportExportBaseInfoVo(String type, Object primaryKey) {
        this.type = type;
        this.primaryKey = primaryKey;
    }

    public ImportExportBaseInfoVo(String type, Object primaryKey, String name) {
        this.type = type;
        this.primaryKey = primaryKey;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Object primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

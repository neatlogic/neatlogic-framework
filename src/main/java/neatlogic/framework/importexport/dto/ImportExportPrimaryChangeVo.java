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

public class ImportExportPrimaryChangeVo {
    private String type;
    private Object oldPrimaryKey;
    private Object newPrimaryKey;

    public ImportExportPrimaryChangeVo() {
    }

    public ImportExportPrimaryChangeVo(String type, Object oldPrimaryKey, Object newPrimaryKey) {
        this.type = type;
        this.oldPrimaryKey = oldPrimaryKey;
        this.newPrimaryKey = newPrimaryKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getOldPrimaryKey() {
        return oldPrimaryKey;
    }

    public void setOldPrimaryKey(Object oldPrimaryKey) {
        this.oldPrimaryKey = oldPrimaryKey;
    }

    public Object getNewPrimaryKey() {
        return newPrimaryKey;
    }

    public void setNewPrimaryKey(Object newPrimaryKey) {
        this.newPrimaryKey = newPrimaryKey;
    }
}

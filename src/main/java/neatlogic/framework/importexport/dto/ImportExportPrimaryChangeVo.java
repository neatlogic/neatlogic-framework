/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

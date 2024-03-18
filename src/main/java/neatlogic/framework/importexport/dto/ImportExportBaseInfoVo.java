/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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

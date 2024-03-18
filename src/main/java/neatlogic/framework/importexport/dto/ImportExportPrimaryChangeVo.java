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

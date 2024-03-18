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

package neatlogic.framework.fulltextindex.dto.fulltextindex;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;

public class FullTextIndexTypeVo extends BasePageVo {
    @EntityField(name = "模块id", type = ApiParamType.STRING)
    private String moduleId;
    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "类型名称", type = ApiParamType.STRING)
    private String typeName;
    @EntityField(name = "激活全局检索", type = ApiParamType.BOOLEAN)
    private boolean activeGlobalSearch;

    public FullTextIndexTypeVo(String _moduleId, String _type, String _typeName, boolean _activeGlobalSearch) {
        moduleId = _moduleId;
        type = _type;
        typeName = _typeName;
        activeGlobalSearch = _activeGlobalSearch;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isActiveGlobalSearch() {
        return activeGlobalSearch;
    }

    public void setActiveGlobalSearch(boolean activeGlobalSearch) {
        this.activeGlobalSearch = activeGlobalSearch;
    }
}

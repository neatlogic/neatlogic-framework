/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

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

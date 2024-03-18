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

package neatlogic.framework.scheduler.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.restful.annotation.EntityField;

public class JobClassVo extends BasePageVo {
    public final static String PUBLIC = "public";
    public final static String PRIVATE = "private";
    @EntityField(name = "定时作业组件名称",
            type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "作业组件类型：public（公开）|private（私有），公开组件允许用户自行配作业",
            type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "定时作业组件全类名",
            type = ApiParamType.STRING)
    private String className;
    @EntityField(name = "定时作业组件所属模块id",
            type = ApiParamType.STRING)
    private String moduleId;
    @EntityField(name = "定时作业组件所属模块名称",
            type = ApiParamType.STRING)
    private String moduleName;

    public JobClassVo() {
        this.setPageSize(20);
    }

    public JobClassVo(String classname, String moduleId) {
        this.setPageSize(20);
        this.className = classname;
        this.moduleId = moduleId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClassName(String classpath) {
        this.className = classpath;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        if (moduleName != null) {
            return moduleName;
        }
        if (moduleId == null) {
            return null;
        }
        ModuleVo module = ModuleUtil.getModuleById(moduleId);
        if (module == null) {
            return null;
        }
        moduleName = module.getName();
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

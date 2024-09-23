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

package neatlogic.framework.dto.module;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.$;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModuleGroupVo implements Serializable {

    @EntityField(name = "模块分组", type = ApiParamType.STRING)
    private String group;
    @EntityField(name = "模块分组名称", type = ApiParamType.STRING)
    private String groupName;
    @EntityField(name = "模块分组排序", type = ApiParamType.INTEGER)
    private Integer groupSort;
    @EntityField(name = "模块分组描述", type = ApiParamType.STRING)
    private String groupDescription;
    @EntityField(name = "模块列表", type = ApiParamType.JSONOBJECT)
    private List<ModuleVo> moduleList;

    @EntityField(name = "模块版本", type = ApiParamType.STRING)
    private String version;

    @EntityField(name = "是否初始化默认数据", type = ApiParamType.BOOLEAN)
    private Boolean isInitDml = false;
    @EntityField(name = "是否存在初始化默认数据文件", type = ApiParamType.BOOLEAN)
    private Boolean isHasDml = false;

    public ModuleGroupVo() {

    }

    public ModuleGroupVo(String _group) {
        group = _group;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroupName() {
        return $.t(groupName);
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<ModuleVo> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<ModuleVo> moduleList) {
        this.moduleList = moduleList;
    }

    public String getGroupDescription() {
        return $.t(groupDescription);
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public Integer getGroupSort() {
        return groupSort;
    }

    public void setGroupSort(Integer groupSort) {
        this.groupSort = groupSort;
    }

    public List<String> getModuleIdList() {
        List<String> moduleIdList = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(this.moduleList)) {
            for (ModuleVo moduleVo : this.moduleList) {
                moduleIdList.add(moduleVo.getId());
            }
        }
        return moduleIdList;
    }


    public Boolean getIsInitDml() {
        return isInitDml;
    }

    public void setIsInitDml(Boolean isInitDml) {
        this.isInitDml = isInitDml;
    }

    public Boolean getIsHasDml() {
        return isHasDml;
    }

    public void setIsHasDml(Boolean isHasDml) {
        this.isHasDml = isHasDml;
    }
}

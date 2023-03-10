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

package neatlogic.framework.dto.module;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.initialdata.core.InitialDataManager;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

public class ModuleVo {
    @EntityField(name = "模块id", type = ApiParamType.STRING)
    private String id;
    @EntityField(name = "模块名", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "模块描述", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "模块版本", type = ApiParamType.STRING)
    private String version;
    @EntityField(name = "模块分组", type = ApiParamType.STRING)
    private String group;
    @EntityField(name = "分组名称", type = ApiParamType.STRING)
    private String groupName;
    @EntityField(name = "分组描述", type = ApiParamType.STRING)
    private String groupDescription;
    @EntityField(name = "分组排序", type = ApiParamType.INTEGER)
    private int groupSort;
    @EntityField(name = "是否包含初始化数据", type = ApiParamType.BOOLEAN)
    private Boolean hasInitialData;
    @EntityField(name = "闭源uuid", type = ApiParamType.STRING)
    private String closedSourceUniqueKey;

    public boolean getHasInitialData() {
        if (hasInitialData == null && StringUtils.isNotBlank(this.id)) {
            hasInitialData = InitialDataManager.hasInitialData(this.id);
        }
        return hasInitialData;
    }

    public void setHasInitialData(boolean hasInitialData) {
        this.hasInitialData = hasInitialData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupSort() {
        return groupSort;
    }

    public void setGroupSort(int groupSort) {
        this.groupSort = groupSort;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getClosedSourceUniqueKey() {
        return closedSourceUniqueKey;
    }

    public void setClosedSourceUniqueKey(String closedSourceUniqueKey) {
        this.closedSourceUniqueKey = closedSourceUniqueKey;
    }
}

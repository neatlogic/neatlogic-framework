/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.module;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.initialdata.core.InitialDataManager;
import codedriver.framework.restful.annotation.EntityField;
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


}
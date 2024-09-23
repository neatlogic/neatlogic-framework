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
import neatlogic.framework.initialdata.core.InitialDataManager;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

public class ModuleVo implements Serializable {
    @EntityField(name = "模块id", type = ApiParamType.STRING)
    private String id;
    @EntityField(name = "模块名", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "模块描述", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "模块版本", type = ApiParamType.STRING)
    private String version;

    @EntityField(name = "模块changelog版本", type = ApiParamType.STRING)
    private String changelogVersion;
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

    @EntityField(name = "闭源uuid", type = ApiParamType.STRING)
    private String urlMapping;

    @EntityField(name = "classpath", type = ApiParamType.STRING)
    private String path;
    @EntityField(name = "父模块", type = ApiParamType.STRING)
    private String parent;
    @EntityField(name = "是否商业模块", type = ApiParamType.BOOLEAN)
    private boolean isCommercial;
    @EntityField(name = "模块修改时间，即jar的最后修改时间", type = ApiParamType.LONG)
    private Date lastModified;

    public ModuleVo() {
    }

    public ModuleVo(String id, String name, String urlMapping, String moduleDescription, String group, String groupName, String groupSort, String groupDescription, String path, String parent, boolean isCommercial) {
        this.id = id;
        this.name = name;
        this.urlMapping = urlMapping;
        this.description = moduleDescription;
        this.group = group;
        this.groupName = groupName;
        this.groupSort = Integer.parseInt(groupSort);
        this.groupDescription = groupDescription;
        this.path = path;
        this.parent = parent;
        this.isCommercial = isCommercial;
    }


    public boolean getHasInitialData() {
        if (hasInitialData == null && StringUtils.isNotBlank(this.id)) {
            hasInitialData = InitialDataManager.hasInitialData(this.id);
        }
        return hasInitialData;
    }

    public void setHasInitialData(Boolean hasInitialData) {
        this.hasInitialData = hasInitialData;
    }

    public boolean isCommercial() {
        return isCommercial;
    }

    public void setCommercial(boolean commercial) {
        isCommercial = commercial;
    }

    public void setHasInitialData(boolean hasInitialData) {
        this.hasInitialData = hasInitialData;
    }

    public String getName() {
        return $.t(name);
    }

    public String getNameWithoutTranslate() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return $.t(description);
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
        return $.t(groupName);
    }

    public String getGroupNameWithoutTranslate() {
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

    public String getUrlMapping() {
        return urlMapping;
    }

    public void setUrlMapping(String urlMapping) {
        this.urlMapping = urlMapping;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getChangelogVersion() {
        return changelogVersion;
    }

    public void setChangelogVersion(String changelogVersion) {
        this.changelogVersion = changelogVersion;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}

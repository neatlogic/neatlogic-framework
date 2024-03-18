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

package neatlogic.framework.restful.groupsearch.core;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.List;

public class GroupSearchVo {

    @EntityField(name = "common.keyword", type = ApiParamType.STRING)
    private String keyword;
    @EntityField(name = "common.defaultvaluelist", type = ApiParamType.JSONARRAY)
    private List<String> valueList;
    @EntityField(name = "common.excludelist", type = ApiParamType.JSONARRAY)
    private List<String> excludeList;
    @EntityField(name = "common.includelist", type = ApiParamType.JSONARRAY)
    private List<String> includeList;
    @EntityField(name = "common.grouplist", type = ApiParamType.JSONARRAY)
    private List<String> groupList;
    @EntityField(name = "common.rangelist", type = ApiParamType.JSONARRAY)
    private List<String> rangeList;
    @EntityField(name = "common.rownum", type = ApiParamType.INTEGER)
    private Integer total;
    @EntityField(name = "term.rdm.projectid", type = ApiParamType.LONG)
    private Long projectId;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    public List<String> getExcludeList() {
        return excludeList;
    }

    public void setExcludeList(List<String> excludeList) {
        this.excludeList = excludeList;
    }

    public List<String> getIncludeList() {
        return includeList;
    }

    public void setIncludeList(List<String> includeList) {
        this.includeList = includeList;
    }

    public List<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }

    public List<String> getRangeList() {
        return rangeList;
    }

    public void setRangeList(List<String> rangeList) {
        this.rangeList = rangeList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}

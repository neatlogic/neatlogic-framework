/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.restful.groupsearch.core;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.List;

public class GroupSearchVo extends BasePageVo {

//    @EntityField(name = "common.keyword", type = ApiParamType.STRING)
//    private String keyword;
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

//    public String getKeyword() {
//        return keyword;
//    }
//
//    public void setKeyword(String keyword) {
//        this.keyword = keyword;
//    }

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

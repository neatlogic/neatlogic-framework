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
import neatlogic.framework.restful.annotation.EntityField;

import java.util.List;

public class GroupSearchGroupVo {

//    @EntityField(name = "common.index", type = ApiParamType.INTEGER)
//    private Integer index;
//    @EntityField(name = "common.islimit", type = ApiParamType.BOOLEAN)
//    private Boolean isLimit;
//    @EntityField(name = "common.hasmore", type = ApiParamType.BOOLEAN)
//    private Boolean isMore;
    @EntityField(name = "common.sort", type = ApiParamType.INTEGER)
    private Integer sort;
    @EntityField(name = "common.value", type = ApiParamType.STRING)
    private String value;
    @EntityField(name = "common.text", type = ApiParamType.STRING)
    private String text;

    private List<GroupSearchOptionVo> dataList;


    @EntityField(name = "common.pagesize", type = ApiParamType.INTEGER)
    private Integer pageSize = 20;

    @EntityField(name = "common.currentpage", type = ApiParamType.INTEGER)
    private Integer currentPage = 1;

    @EntityField(name = "common.pagecount", type = ApiParamType.INTEGER)
    private Integer pageCount = 0;

    @EntityField(name = "common.rownum", type = ApiParamType.INTEGER)
    private Integer rowNum = 0;

//    public Integer getIndex() {
//        return index;
//    }
//
//    public void setIndex(Integer index) {
//        this.index = index;
//    }
//
//    public Boolean getIsLimit() {
//        return isLimit;
//    }
//
//    public void setIsLimit(Boolean limit) {
//        isLimit = limit;
//    }
//
//    public Boolean getIsMore() {
//        return isMore;
//    }
//
//    public void setIsMore(Boolean more) {
//        isMore = more;
//    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<GroupSearchOptionVo> getDataList() {
        return dataList;
    }

    public void setDataList(List<GroupSearchOptionVo> dataList) {
        this.dataList = dataList;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }
}

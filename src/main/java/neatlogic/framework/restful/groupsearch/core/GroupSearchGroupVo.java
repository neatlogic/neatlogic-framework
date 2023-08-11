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

package neatlogic.framework.restful.groupsearch.core;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.List;

public class GroupSearchGroupVo {

    @EntityField(name = "common.index", type = ApiParamType.INTEGER)
    private Integer index;
    @EntityField(name = "common.islimit", type = ApiParamType.BOOLEAN)
    private Boolean isLimit;
    @EntityField(name = "common.hasmore", type = ApiParamType.BOOLEAN)
    private Boolean isMore;
    @EntityField(name = "common.sort", type = ApiParamType.INTEGER)
    private Integer sort;
    @EntityField(name = "common.value", type = ApiParamType.STRING)
    private String value;
    @EntityField(name = "common.text", type = ApiParamType.STRING)
    private String text;

    private List<GroupSearchOptionVo> dataList;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(Boolean limit) {
        isLimit = limit;
    }

    public Boolean getIsMore() {
        return isMore;
    }

    public void setIsMore(Boolean more) {
        isMore = more;
    }

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
}

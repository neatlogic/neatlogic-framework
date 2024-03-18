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

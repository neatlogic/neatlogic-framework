/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.dto.teamtag;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class TeamTagTeamVo extends BasePageVo {

    @EntityField(name = "标签id", type = ApiParamType.LONG)
    private Long tagId;
    @EntityField(name = "分组uuid", type = ApiParamType.STRING)
    private String teamUuid;
    @JSONField(serialize = false)
    private Long updateTime;//更新标识
    @EntityField(name = "是否包含子分组", type = ApiParamType.INTEGER)
    private Integer checkedChildren;
    @JSONField(serialize = false)
    private List<Long> tagIdList;

    public TeamTagTeamVo() {
    }

    public TeamTagTeamVo(List<Long> tagIdList, String teamUuid, Integer checkedChildren, Long updateTime) {
        this.tagIdList = tagIdList;
        this.teamUuid = teamUuid;
        this.checkedChildren = checkedChildren;
        this.updateTime = updateTime;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTeamUuid() {
        return teamUuid;
    }

    public void setTeamUuid(String teamUuid) {
        this.teamUuid = teamUuid;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getCheckedChildren() {
        return checkedChildren;
    }

    public void setCheckedChildren(Integer checkedChildren) {
        this.checkedChildren = checkedChildren;
    }

    public List<Long> getTagIdList() {
        return tagIdList;
    }

    public void setTagIdList(List<Long> tagIdList) {
        this.tagIdList = tagIdList;
    }

    public Integer getTagIdListSize() {
        if(CollectionUtils.isNotEmpty(this.tagIdList)){
            return this.tagIdList.size();
        }
        return 0;
    }
}

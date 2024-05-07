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

package neatlogic.framework.dto.region;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;

public class RegionTeamVo extends BasePageVo {
    @EntityField(name = "地域id", type = ApiParamType.LONG)
    private Long regionId;
    @EntityField(name = "分组uuid", type = ApiParamType.LONG)
    private String teamUuid;
    @EntityField(name = "是否包含子分组", type = ApiParamType.INTEGER)
    private Integer checkedChildren;
    @JSONField(serialize = false)
    private Long updateTime;//更新标识

    public RegionTeamVo() {
    }

    public RegionTeamVo(Long regionId, String teamUuid, Integer checkedChildren, Long updateTime) {
        this.regionId = regionId;
        this.teamUuid = teamUuid;
        this.checkedChildren = checkedChildren;
        this.updateTime = updateTime;
    }

    public RegionTeamVo(Long regionId) {
        this.regionId = regionId;
    }


    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getTeamUuid() {
        return teamUuid;
    }

    public void setTeamUuid(String teamUuid) {
        this.teamUuid = teamUuid;
    }

    public Integer getCheckedChildren() {
        return checkedChildren;
    }

    public void setCheckedChildren(Integer checkedChildren) {
        this.checkedChildren = checkedChildren;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}

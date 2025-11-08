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
    @EntityField(name = "是否包含子分组", type = ApiParamType.LONG)
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

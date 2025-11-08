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

package neatlogic.framework.dto;

import java.io.Serializable;

/**
 * @author linbq
 * @since 2021/7/29 15:34
 **/
public class RoleTeamVo implements Serializable {
    private String roleUuid;
    private String teamUuid;
    private Integer checkedChildren;

    public RoleTeamVo() {

    }

    public RoleTeamVo(String roleUuid, String teamUuid) {
        this.roleUuid = roleUuid;
        this.teamUuid = teamUuid;
    }

    public RoleTeamVo(String roleUuid, String teamUuid, Integer checkedChildren) {
        this.roleUuid = roleUuid;
        this.teamUuid = teamUuid;
        this.checkedChildren = checkedChildren;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
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
}

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

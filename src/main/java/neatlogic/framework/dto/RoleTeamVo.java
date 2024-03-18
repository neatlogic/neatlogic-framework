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

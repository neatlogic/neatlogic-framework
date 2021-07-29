/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto;

/**
 * @author linbq
 * @since 2021/7/29 15:34
 **/
public class RoleTeamVo {
    private String roleUuid;
    private String teamUuid;

    public RoleTeamVo() {

    }
    public RoleTeamVo(String roleUuid, String teamUuid) {
        this.roleUuid = roleUuid;
        this.teamUuid = teamUuid;
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
}

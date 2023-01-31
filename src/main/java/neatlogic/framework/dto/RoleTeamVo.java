/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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

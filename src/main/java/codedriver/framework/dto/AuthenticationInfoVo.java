/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto;

import java.util.List;

/**
 * @author linbq
 * @since 2021/8/2 20:19
 **/
public class AuthenticationInfoVo {
    private String userUuid;
    private List<String> userUuidList;
    private List<String> teamUuidList;
    private List<String> roleUuidList;

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public List<String> getUserUuidList() {
        return userUuidList;
    }

    public void setUserUuidList(List<String> userUuidList) {
        this.userUuidList = userUuidList;
    }

    public List<String> getTeamUuidList() {
        return teamUuidList;
    }

    public void setTeamUuidList(List<String> teamUuidList) {
        this.teamUuidList = teamUuidList;
    }

    public List<String> getRoleUuidList() {
        return roleUuidList;
    }

    public void setRoleUuidList(List<String> roleUuidList) {
        this.roleUuidList = roleUuidList;
    }
}
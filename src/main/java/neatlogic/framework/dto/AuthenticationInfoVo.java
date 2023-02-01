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

import org.apache.commons.collections4.CollectionUtils;

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

    public boolean validUser(List<String> userUuidList) {
        return userUuidList.contains(userUuid);
    }

    public boolean validTeam(List<String> pTeamUuidList) {
        if (CollectionUtils.isNotEmpty(pTeamUuidList)) {
            for (String teamUuid : pTeamUuidList) {
                if (teamUuidList.stream().anyMatch(d -> d.equals(teamUuid))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean validRole(List<String> pRoleUuidList) {
        if (CollectionUtils.isNotEmpty(pRoleUuidList)) {
            for (String roleUuid : pRoleUuidList) {
                if (roleUuidList.stream().anyMatch(d -> d.equals(roleUuid))) {
                    return true;
                }
            }
        }
        return false;
    }

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

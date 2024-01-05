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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author linbq
 * @since 2021/8/2 20:19
 **/
public class AuthenticationInfoVo{
    private String userUuid;
    private final List<String> userUuidList = new ArrayList<>();
    private final List<String> teamUuidList = new ArrayList<>();
    private final List<String> roleUuidList = new ArrayList<>();
    private Set<String> headerSet = new HashSet<>(); //使用到的header


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
    public AuthenticationInfoVo() {

    }

    public AuthenticationInfoVo(String userUuid) {
        this.userUuid = userUuid;
    }

    public AuthenticationInfoVo(String userUuid, List<String> teamUuidList, List<String> roleUuidList, Set<String> headerSet ) {
        this.userUuid = userUuid;
        this.teamUuidList.addAll(teamUuidList);
        this.roleUuidList.addAll(roleUuidList);
        this.headerSet.addAll(headerSet);
    }

    public AuthenticationInfoVo(List<String> userUuidList, List<String> teamUuidList, List<String> roleUuidList, Set<String> headerSet) {
        this.userUuidList.addAll(userUuidList);
        this.teamUuidList.addAll(teamUuidList);
        this.roleUuidList.addAll(roleUuidList);
        this.headerSet.addAll(headerSet);
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

    public List<String> getTeamUuidList() {
        return teamUuidList;
    }

    public List<String> getRoleUuidList() {
        return roleUuidList;
    }

    public Set<String> getHeaderSet() {
        return headerSet;
    }

    public void setHeaderSet(Set<String> headerSet) {
        this.headerSet = headerSet;
    }
}

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

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author linbq
 * @since 2021/8/2 20:19
 **/
public class AuthenticationInfoVo {
    private String userUuid;
    private final List<String> userUuidList = new ArrayList<>();
    private final List<String> teamUuidList = new ArrayList<>();
    private final List<String> roleUuidList = new ArrayList<>();
    private Set<String> headerSet = new HashSet<>(); //使用到的header
    private JSONObject originHeader = new JSONObject(); //原始请求的header


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

    public AuthenticationInfoVo(String userUuid, List<String> teamUuidList, List<String> roleUuidList, Set<String> headerSet, JSONObject originHeader) {
        this.userUuid = userUuid;
        this.teamUuidList.addAll(teamUuidList);
        this.roleUuidList.addAll(roleUuidList);
        this.headerSet.addAll(headerSet);
        this.originHeader = originHeader;
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

    public JSONObject getOriginHeader() {
        return originHeader;
    }

    public void setOriginHeader(JSONObject originHeader) {
        this.originHeader = originHeader;
    }

    public boolean isNotNull() {
        return CollectionUtils.isNotEmpty(userUuidList) || CollectionUtils.isNotEmpty(teamUuidList) || CollectionUtils.isNotEmpty(roleUuidList) || MapUtils.isNotEmpty(originHeader) || CollectionUtils.isNotEmpty(headerSet);
    }
}

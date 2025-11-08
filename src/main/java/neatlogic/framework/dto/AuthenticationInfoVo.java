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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author linbq
 * @since 2021/8/2 20:19
 **/
public class AuthenticationInfoVo implements Serializable {
    private static final long serialVersionUID = -8846808868047516668L;
    private String userUuid;
    private final List<String> userUuidList = new ArrayList<>();
    private final List<String> teamUuidList = new ArrayList<>();
    private final List<String> roleUuidList = new ArrayList<>();
    @JSONField(serialize = false)
    private final Set<String> headerSet = new HashSet<>(); //使用到的header
    private JSONObject originHeader = new JSONObject(); //原始请求的header

    public AuthenticationInfoVo(Set<String> headerSet) {
        this.headerSet.addAll(headerSet);
    }

    public AuthenticationInfoVo copy() {
        AuthenticationInfoVo vo = new AuthenticationInfoVo();
        vo.userUuid = this.userUuid;
        vo.userUuidList.addAll(userUuidList);
        vo.teamUuidList.addAll(teamUuidList);
        vo.roleUuidList.addAll(roleUuidList);
        if (CollectionUtils.isNotEmpty(headerSet)) {
            vo.headerSet.addAll(headerSet);
        }
        return vo;
    }

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
        if (CollectionUtils.isNotEmpty(headerSet)) {
            this.headerSet.addAll(headerSet);
        }
        this.originHeader = originHeader;
    }

    public AuthenticationInfoVo(List<String> userUuidList, List<String> teamUuidList, List<String> roleUuidList, Set<String> headerSet) {
        this.userUuidList.addAll(userUuidList);
        this.teamUuidList.addAll(teamUuidList);
        this.roleUuidList.addAll(roleUuidList);
        if (CollectionUtils.isNotEmpty(headerSet)) {
            this.headerSet.addAll(headerSet);
        }
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

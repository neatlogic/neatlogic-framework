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

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class RoleVo extends BasePageVo implements Serializable {

    private static final long serialVersionUID = -8007028390813552667L;

    public static final String USER_DEFAULT_ROLE = "R_SYSTEM_USER";
    private Long id;
    @EntityField(name = "角色uuid",
            type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "角色名称",
            type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "角色描述",
            type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "生效规则",
            type = ApiParamType.STRING)
    private String rule;
    private int userCount;
    private int teamCount;
    private String authGroup;
    private String auth;
    private List<String> userUuidList;
    private List<RoleAuthVo> roleAuthList;
    @JSONField(serialize = false)
    private List<String> roleUuidList;

    @EntityField(name = "角色所在分组的组信息列表", type = ApiParamType.JSONARRAY)
    private List<TeamVo> teamList;

    /**
     * 此字段专供前端使用，用于渲染头像时区分对象类型，取值范围[user,team,role]
     */
    @EntityField(name = "前端初始化类型，取值范围[user,team,role]", type = ApiParamType.STRING)
    private final String initType = GroupSearch.ROLE.getValue();

    public RoleVo() {
    }

    public RoleVo(String uuid) {
        this.uuid = uuid;
    }

    public List<RoleAuthVo> getRoleAuthList() {
        return roleAuthList;
    }

    public List<String> getUserUuidList() {
        return userUuidList;
    }

    public void setUserUuidList(List<String> userUuidList) {
        this.userUuidList = userUuidList;
    }

    public void setRoleAuthList(List<RoleAuthVo> roleAuthList) {
        this.roleAuthList = roleAuthList;
    }

    public String getAuthGroup() {
        return authGroup;
    }

    public void setAuthGroup(String authGroup) {
        this.authGroup = authGroup;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public synchronized String getUuid() {
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString().replace("-", "");
        }
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    public String getInitType() {
        return initType;
    }

    public List<String> getRoleUuidList() {
        return roleUuidList;
    }

    public void setRoleUuidList(List<String> roleUuidList) {
        this.roleUuidList = roleUuidList;
    }


    public List<TeamVo> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<TeamVo> teamList) {
        this.teamList = teamList;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}

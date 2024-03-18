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

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class RoleVo extends BasePageVo implements Serializable {

    private static final long serialVersionUID = -8007028390813552667L;

    public static final String USER_DEFAULT_ROLE = "R_SYSTEM_USER";
    private Long id;
    @EntityField(name = "common.uuid",
            type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "common.name",
            type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "common.description",
            type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "common.rule",
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
    @EntityField(name = "common.inittype", type = ApiParamType.STRING)
    private final String initType = GroupSearch.ROLE.getValue();

    @EntityField(name = "common.isdeleted", type = ApiParamType.ENUM)
    private Integer isDelete;

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

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}

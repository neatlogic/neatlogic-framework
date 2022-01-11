package codedriver.framework.service;

import codedriver.framework.dto.RoleVo;

import java.util.List;

public interface RoleService {

    /**
     * 补充角色的分组数量和用户数量
     * @param roleList
     */
    void setRoleTeamCountAndRoleUserCount(List<RoleVo> roleList);

}

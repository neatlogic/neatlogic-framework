package codedriver.framework.service;

import codedriver.framework.dto.RoleVo;

import java.util.List;

public interface RoleService {

    void getRoleTeamAndRoleUser(List<RoleVo> roleList);

}

package codedriver.framework.service;

import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dto.RoleVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public void getRoleTeamCountAndRoleUserCount(List<RoleVo> roleList) {
        if (CollectionUtils.isNotEmpty(roleList)) {
            Map<String, RoleVo> roleVoMap = roleList.stream().collect(Collectors.toMap(RoleVo::getUuid, e -> e));
            List<String> roleUuidLIst = roleList.stream().map(RoleVo::getUuid).collect(Collectors.toList());
            //补充角色的分组数量
            List<RoleVo> teamCountList = roleMapper.getTeamCountByRoleUuidList(roleUuidLIst);
            teamCountList.forEach(e -> roleVoMap.get(e.getUuid()).setTeamCount(e.getTeamCount()));
            //补充角色的用户数量
            List<RoleVo> userCountList = roleMapper.getUserCountByRoleUuidList(roleUuidLIst);
            userCountList.forEach(e -> roleVoMap.get(e.getUuid()).setUserCount(e.getUserCount()));
        }
    }
}

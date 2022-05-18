/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.service;

import codedriver.framework.dto.UserVo;

import java.util.List;
import java.util.Set;

public interface UserService {

    /**
     * @Description: 根据用户uuid集合与分组uuid集合查询激活的用户uuid
     * @Author: laiwt
     * @Date: 2021/3/5 17:06
     * @Params: [userUuidList, teamUuidList]
     * @Returns: java.util.Set<java.lang.String>
     **/
    Set<String> getUserUuidSetByUserUuidListAndTeamUuidList(List<String> userUuidList, List<String> teamUuidList);

    /**
     * 根据指定范围获取用户
     *
     * @param userVo    用户参数
     * @param rangeList 范围
     */
    void getUserByRangeList(UserVo userVo, List<String> rangeList);

    List<UserVo> getUserListByRoleUuid(String roleUuid);

    /**
     * 根据角色uuid获取其下所有用户uuid，包括分组中的用户
     *
     * @param roleUuid 角色uuid
     * @return
     */
    Set<String> getUserUuidSetByRoleUuid(String roleUuid);

    /**
     * 根据角色uuid获取其关联的所有分组uuid，如果关联的组需要穿透，则找出其所有子分组uuid
     *
     * @param roleUuid 角色uuid
     * @return
     */
    Set<String> getRoleTeamUuidSet(String roleUuid);

}

/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.core;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.init.MaintenanceMode;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dao.mapper.UserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RootComponent
public class AuthActionChecker {
    private static RoleMapper roleMapper;

    private static UserMapper userMapper;

    @Autowired
    public void setRoleMapper(RoleMapper _roleMapper) {
        roleMapper = _roleMapper;
    }

    @Autowired
    public void setUserMapper(UserMapper _userMapper) {
        userMapper = _userMapper;
    }

    public static Boolean check(String... action) {
        if (action == null || action.length == 0) {
            return false;
        }
        UserContext userContext = UserContext.get();
        List<String> actionList = new ArrayList<>(Arrays.asList(action));
        //无需鉴权注解 || 维护模式下，维护用户，指定权限不需要鉴权
        if (Config.ENABLE_SUPERADMIN() && userContext.getUserUuid().equals(MaintenanceMode.MAINTENANCE_USER) && MaintenanceMode.maintenanceAuthSet.containsAll(actionList)) {
            return true;
        }

        if (userContext != null) {
            List<String> roleUuidList = roleMapper.getRoleUuidListByAuth(actionList);
            if (roleUuidList != null && roleUuidList.size() > 0) {
                for (String roleUuid : roleUuidList) {
                    if (null != userContext.getRoleUuidList() && userContext.getRoleUuidList().contains(roleUuid)) {
                        return true;
                    }
                }
            }
            return userMapper.checkUserAuthorityIsExists(userContext.getUserUuid(true), actionList) > 0;
        } else {
            return false;
        }
    }

    public static Boolean checkByUserUuid(String userUuid, String... action) {
        if (action == null || action.length == 0) {
            return false;
        }
        List<String> actionList = Arrays.asList(action);
        List<String> actionRoleUuidList = roleMapper.getRoleUuidListByAuth(actionList);
        if (CollectionUtils.isNotEmpty(actionRoleUuidList)) {
            List<String> roleUuidList = userMapper.getRoleUuidListByUserUuid(userUuid);
            for (String roleUuid : actionRoleUuidList) {
                if (roleUuidList.contains(roleUuid)) {
                    return true;
                }
            }
        }
        return userMapper.checkUserAuthorityIsExists(userUuid, actionList) > 0;
    }
}

package codedriver.framework.auth.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.RoleMapper;
import codedriver.framework.dao.mapper.UserMapper;

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
        List<String> actionList = new ArrayList<>();
        for (String a : action) {
            actionList.add(a);
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
            if (userMapper.checkUserAuthorityIsExists(userContext.getUserUuid(true), actionList) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}

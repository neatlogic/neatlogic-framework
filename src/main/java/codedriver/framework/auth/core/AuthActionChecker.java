package codedriver.framework.auth.core;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.label.NO_AUTH;
import codedriver.framework.common.RootComponent;
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

    /**
     * create by: 89770
     * description: TODO
     * create time: 2020/12/23 10:57
     * 
     * @return 
     */
    public static Boolean check(String... action) {
        if (action == null || action.length == 0) {
            return false;
        }
        UserContext userContext = UserContext.get();
        List<String> actionList = new ArrayList<>();
        for (String a : action) {
            actionList.add(a);
        }
        //无需鉴权
        if(actionList.contains(NO_AUTH.class.getSimpleName())){
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
            if (userMapper.checkUserAuthorityIsExists(userContext.getUserUuid(true), actionList) > 0) {
                return true;
            } else {
                return false;
            }
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
        if(CollectionUtils.isNotEmpty(actionRoleUuidList)) {
            List<String> roleUuidList = userMapper.getRoleUuidListByUserUuid(userUuid);
            for (String roleUuid : actionRoleUuidList) {
                if(roleUuidList.contains(roleUuid)) {
                    return true;
                }
            }
        }
        return userMapper.checkUserAuthorityIsExists(userUuid, actionList) > 0;
    }
}

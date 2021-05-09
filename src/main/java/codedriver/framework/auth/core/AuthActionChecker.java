/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.core;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.auth.init.MaintenanceMode;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserAuthVo;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RootComponent
public class AuthActionChecker {

    private static UserMapper userMapper;

    @Resource
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
            return checkByUserUuid(userContext.getUserUuid(), actionList);
        } else {
            return false;
        }
    }

    /**
     * 穿透校验该用户是拥有在满足的权限
     *
     * @param userUuid 当前登录人
     * @param action   目标权限
     * @return 是否有权限 有：true 否：false
     */
    public static Boolean checkByUserUuid(String userUuid, String... action) {
        if (action == null || action.length == 0) {
            return false;
        }
        List<String> actionList = Arrays.asList(action);
        return checkByUserUuid(userUuid, actionList);
    }

    /**
     * 穿透校验该用户是拥有在满足的权限
     * 1、递归获取该用户所有权限
     * 2、比对用户所有权限中是否包含需要检验的权限
     *
     * @param userUuid   当前登录人
     * @param actionList 目标权限
     * @return 是否有权限 有：true 否：false
     */
    public static Boolean checkByUserUuid(String userUuid, List<String> actionList) {
        List<UserAuthVo> userAuthVoList = userMapper.searchUserAllAuthByUserAuthCache(new UserAuthVo(userUuid));
        List<String> userAuthList = userAuthVoList.stream().map(UserAuthVo::getAuth).collect(Collectors.toList());
        //判断从数据库查询的用户权限是否满足
        List<String> contains = userAuthList.stream().filter(actionList::contains).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(contains)){
            return true;
        }
        //以上不满足，则遍历递归所有权限寻找
        for (int i = 0; i < userAuthList.size(); i++) { //只能用下标索引，否则会报java.util.ConcurrentModificationException 因为for循环里会add元素
            if (checkAuthList(userAuthList.get(i), userAuthList, actionList)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 递归获取权限
     *
     * @param auth     权限
     * @param authList 当前登录人所有权限列表
     * @param actionList  目标权限
     * @return 存在权限 是：true 否：false
     */
    private static boolean checkAuthList(String auth, List<String> authList, List<String> actionList) {
        AuthBase authBase = AuthFactory.getAuthInstance(auth.toUpperCase(Locale.ROOT));
        if (authBase != null) {
            if (actionList.contains(authBase.getAuthName())) {
                return true;
            }
            List<Class<? extends AuthBase>> authClassList = authBase.getIncludeAuths();
            for (Class<? extends AuthBase> authClass : authClassList) {
                if (!authList.contains(authClass.getSimpleName())) {//防止回环
                    authList.add(authClass.getSimpleName());
                    return checkAuthList(authClass.getSimpleName(), authList, actionList);
                }
            }
        }
        return false;
    }

}

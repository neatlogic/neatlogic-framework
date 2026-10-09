/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.auth.core;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.init.MaintenanceMode;
import neatlogic.framework.auth.label.NoAuth;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.systemuser.SystemUserFactory;
import neatlogic.framework.common.constvalue.systemuser.ISystemUser;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserAuthVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.service.AuthenticationInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限校验入口，支持校验当前用户或显式指定用户，权限标识取 AuthBase 类型的简单类名。
 * 参数无效时返回 false；目标权限包含 NoAuth 时直接通过。
 * 系统用户合并代码出厂授权与页面授权，所有执行位置使用相同规则，不按身份直接放行。
 * 维护用户命中任一维护权限时直接通过，否则继续校验权限。
 * 普通目标用户与当前上下文用户一致时，信任并复用 UserContext 中的超级管理员状态和鉴权信息；
 * 校验其他普通用户时，实时确认目标用户存在、已启用且未删除，再使用目标用户自身的超级管理员状态和鉴权信息。
 * 直接权限未命中时继续递归检查权限包含关系，任一目标权限命中即通过。
 */
@RootComponent
public class AuthActionChecker {

    private static UserMapper userMapper;

    private static AuthenticationInfoService authenticationInfoService;

    @Resource
    public void setUserMapper(UserMapper _userMapper) {
        userMapper = _userMapper;
    }

    @Resource
    public void setAuthenticationInfoService(AuthenticationInfoService _authenticationInfoService) {
        authenticationInfoService = _authenticationInfoService;
    }

    /**
     * 校验当前用户是否拥有任一目标权限。
     *
     * @param actionClass 目标权限类型
     * @return 是否拥有任一目标权限
     */
    @SafeVarargs
    public static Boolean check(Class<? extends AuthBase>... actionClass) {
        UserContext userContext = UserContext.get();
        if (userContext == null || StringUtils.isBlank(userContext.getUserUuid())) {
            return false;
        }
        return check(userContext.getUserUuid(), actionClass);
    }

    /**
     * 校验指定用户是否拥有任一目标权限。
     *
     * @param userUuid    待校验用户 UUID
     * @param actionClass 目标权限类型
     * @return 是否有权限 有：true 否：false
     */
    @SafeVarargs
    public static Boolean check(String userUuid, Class<? extends AuthBase>... actionClass) {
        if (StringUtils.isBlank(userUuid)) {
            return false;
        }
        List<String> actionList = getActionList(actionClass);
        if (CollectionUtils.isEmpty(actionList)) {
            return false;
        }
        return check(userUuid, actionList);
    }

    /**
     * 将权限类型转换为权限名称；存在空权限类型时拒绝本次校验。
     *
     * @param actionClass 目标权限类型
     * @return 权限名称列表，参数无效时返回空列表
     */
    private static List<String> getActionList(Class<? extends AuthBase>[] actionClass) {
        if (actionClass == null || actionClass.length == 0) {
            return Collections.emptyList();
        }
        List<String> actionList = new ArrayList<>();
        for (Class<? extends AuthBase> action : actionClass) {
            if (action == null) {
                return Collections.emptyList();
            }
            actionList.add(action.getSimpleName());
        }
        return actionList;
    }

    /**
     * 校验指定用户是否拥有任一目标权限。
     *
     * @param userUuid   待校验用户 UUID
     * @param actionList 目标权限列表
     * @return 是否拥有任一目标权限
     */
    private static Boolean check(String userUuid, List<String> actionList) {
        if (StringUtils.isBlank(userUuid) || CollectionUtils.isEmpty(actionList)) {
            return false;
        }
        if (actionList.contains(NoAuth.class.getSimpleName())) {
            return true;
        }
        ISystemUser systemUser = SystemUserFactory.getSystemUserByUser(userUuid);
        if (systemUser != null) {
            // 系统身份允许按 ID 定位，但数据库授权始终使用注册 UUID。
            userUuid = systemUser.getUserUuid();
        }
        boolean isMaintenanceUser = Config.ENABLE_MAINTENANCE() && Objects.equals(userUuid, Config.MAINTENANCE());
        if (isMaintenanceUser && !Collections.disjoint(MaintenanceMode.maintenanceAuthSet, actionList)) {
            return true;
        }
        AuthenticationInfoVo authenticationInfoVo;
        UserContext userContext = UserContext.get();
        boolean isCurrentUser = userContext != null && Objects.equals(userContext.getUserUuid(), userUuid);
        if (systemUser != null) {
            // 不读取普通用户表，也不信任系统身份上下文中的超级管理员标记。
            // 代码默认授权不可由页面撤销，命中后无需查询数据库；未命中仍实时读取页面授权。
            List<String> defaultAuthList = AuthFactory.getDefaultAuthListBySystemUser(userUuid).stream()
                    .map(AuthBase::getAuthName).collect(Collectors.toList());
            if (matchesAuthList(defaultAuthList, actionList)) {
                return true;
            }
            authenticationInfoVo = new AuthenticationInfoVo(userUuid);
        } else if (isCurrentUser) {
            if (Boolean.TRUE.equals(userContext.getIsSuperAdmin())) {
                return true;
            }
            authenticationInfoVo = userContext.getAuthenticationInfoVo();
        } else {
            UserVo userVo = userMapper.getUserBaseInfoByUuidWithoutCache(userUuid);
            if (userVo == null || !Objects.equals(userVo.getIsActive(), 1) || Objects.equals(userVo.getIsDelete(), 1)) {
                return false;
            }
            if (Boolean.TRUE.equals(userVo.getIsSuperAdmin())) {
                return true;
            }
            authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userUuid);
        }

        List<UserAuthVo> userAuthVoList = userMapper.searchUserAllAuthByUserAuth(authenticationInfoVo);
        List<String> userAuthList = userAuthVoList.stream().map(UserAuthVo::getAuth).collect(Collectors.toList());
        return matchesAuthList(userAuthList, actionList);
    }

    /** 代码授权与页面授权复用同一包含关系判断，传入独立列表以隔离遍历追加结果。 */
    private static boolean matchesAuthList(List<String> userAuthList, List<String> actionList) {
        if (userAuthList.stream().anyMatch(actionList::contains)) {
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
     * @param auth       权限
     * @param authList   当前登录人所有权限列表
     * @param actionList 目标权限
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
                    if (checkAuthList(authClass.getSimpleName(), authList, actionList)) {//防止漏找后续的include权限，故不能直接return
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 根据用户权限穿透获取所有权限
     *
     * @param userAuthList 未穿透的权限
     */
    public static void getAuthList(List<UserAuthVo> userAuthList) {
        for (int i = 0; i < userAuthList.size(); i++) {
            AuthBase authBase = AuthFactory.getAuthInstance(userAuthList.get(i).getAuth().toUpperCase(Locale.ROOT));
            if (authBase != null) {
                getUserAuthListByAuth(authBase, userAuthList, userAuthList.get(i).getUserUuid());
            }
        }
    }


    /**
     * 递归穿透获取权限
     *
     * @param authBase     权限对象
     * @param userAuthList 用户对应权限
     * @param userUuid 目标用户 UUID，展开权限不借用当前调用者身份
     */
    private static void getUserAuthListByAuth(AuthBase authBase, List<UserAuthVo> userAuthList, String userUuid) {
        if (authBase != null) {
            List<Class<? extends AuthBase>> authClassList = authBase.getIncludeAuths();
            for (Class<? extends AuthBase> authClass : authClassList) {
                if (userAuthList.stream().noneMatch(o -> Objects.equals(o.getAuth(), authClass.getSimpleName()))) {//防止回环
                    AuthBase auth = AuthFactory.getAuthInstance(authClass.getSimpleName());
                    if (auth != null) {
                        userAuthList.add(new UserAuthVo(userUuid, auth));
                        getUserAuthListByAuth(auth, userAuthList, userUuid);
                    }
                }
            }
        }
    }
}

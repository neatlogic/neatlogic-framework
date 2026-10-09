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

import neatlogic.framework.auth.label.NoAuth;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.systemuser.ISystemUser;
import neatlogic.framework.common.constvalue.systemuser.SystemUserFactory;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.common.util.ModuleUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/** 复用模块权限注册表，提供权限定义与受租户模块边界约束的系统用户默认授权。 */
public class AuthFactory {
    private static final Log logger = LogFactory.getLog(AuthFactory.class);
    private static final Map<String, AuthBase> authMap = new HashMap<>();
    private static final Map<String, List<AuthBase>> authGroupMap = new HashMap<>();

    private static Map<String, List<AuthBase>> defaultAuthMap;

    static {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends AuthBase>> authClass = reflections.getSubTypesOf(AuthBase.class);

        for (Class<? extends AuthBase> c : authClass) {
            try {
                //排除抽象类
                if (!Modifier.isAbstract(c.getModifiers()) && c != NoAuth.class) {
                    AuthBase authIns = c.newInstance();
                    if (ModuleUtil.getModuleGroup(authIns.getAuthGroup()) == null || (authIns instanceof AuthCSBase && ModuleUtil.isModuleInvalidated(authIns.getAuthModule()))) {
                        continue;
                    }
                    authMap.put(authIns.getAuthName(), authIns);
                    if (authGroupMap.containsKey(authIns.getAuthGroup())) {
                        authGroupMap.get(authIns.getAuthGroup()).add(authIns);
                    } else {
                        List<AuthBase> authList = new ArrayList<>();
                        authList.add(authIns);
                        authGroupMap.put(authIns.getAuthGroup(), authList);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        initializeDefaultAuthMap();
    }

    /** 注册完成后索引代码默认授权，排序只执行一次，不保存租户过滤结果。 */
    private static void initializeDefaultAuthMap() {
        Map<String, List<AuthBase>> defaults = new HashMap<>();
        for (AuthBase auth : authMap.values()) {
            Set<String> userUuids = new HashSet<>();
            for (ISystemUser user : auth.getDefaultSystemUserList()) {
                if (userUuids.add(user.getUserUuid())) {
                    defaults.computeIfAbsent(user.getUserUuid(), key -> new ArrayList<>()).add(auth);
                }
            }
        }
        defaults.replaceAll((uuid, auths) -> {
            auths.sort(Comparator.comparing(AuthBase::getAuthGroup).thenComparing(AuthBase::getAuthName));
            return Collections.unmodifiableList(auths);
        });
        defaultAuthMap = Collections.unmodifiableMap(defaults);
    }

    public static List<String> getAuthActionListByAuthGroupList(List<String> authGroupList) {
        List<String> authActionList = new ArrayList<>();
        for (String authGroup : authGroupList) {
            List<AuthBase> authBaseList = authGroupMap.get(authGroup);
            if (CollectionUtils.isNotEmpty(authBaseList)) {
                authActionList.addAll(authBaseList.stream().map(AuthBase::getAuthName).collect(Collectors.toList()));
            }
        }
        return authActionList;
    }

    public static AuthBase getAuthInstance(String authName) {
        return authMap.get(authName);
    }

    public static List<AuthBase> getAuthList() {
        return new ArrayList<>(authMap.values());
    }

    /**
     * 查询注册系统用户的直接出厂权限，沿用已加载权限与当前租户模块边界。
     * 返回独立列表，不展开包含关系，供鉴权和只读授权展示共用。
     */
    public static List<AuthBase> getDefaultAuthListBySystemUser(String userUuid) {
        ISystemUser systemUser = SystemUserFactory.getSystemUserByUser(userUuid);
        if (systemUser == null) {
            return Collections.emptyList();
        }
        List<AuthBase> defaults = defaultAuthMap.get(systemUser.getUserUuid());
        if (CollectionUtils.isEmpty(defaults)) {
            return Collections.emptyList();
        }
        Set<String> activeGroups = TenantContext.get().getActiveModuleMap().values().stream()
                .map(ModuleVo::getGroup).collect(Collectors.toSet());
        List<AuthBase> result = new ArrayList<>();
        for (AuthBase auth : defaults) {
            if (activeGroups.contains(auth.getAuthGroup())) {
                result.add(auth);
            }
        }
        return result;
    }

    public static Map<String, List<AuthBase>> getAuthGroupMap() {
        return authGroupMap;
    }
}

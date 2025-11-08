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
import neatlogic.framework.common.util.ModuleUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class AuthFactory {
    private static final Log logger = LogFactory.getLog(AuthFactory.class);
    private static final Map<String, AuthBase> authMap = new HashMap<>();
    private static final Map<String, List<AuthBase>> authGroupMap = new HashMap<>();

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

    public static Map<String, List<AuthBase>> getAuthGroupMap() {
        return authGroupMap;
    }
}

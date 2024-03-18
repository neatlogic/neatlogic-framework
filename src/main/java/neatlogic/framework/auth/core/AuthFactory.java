/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.auth.core;

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
                if (!Modifier.isAbstract(c.getModifiers())) {
                    AuthBase authIns = c.newInstance();
                    if (ModuleUtil.getModuleGroup(authIns.getAuthGroup()) == null || ModuleUtil.isModuleInvalidated(authIns.getAuthGroup())) {
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

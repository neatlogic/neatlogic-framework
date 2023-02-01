/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.auth.core;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.license.LicenseVo;
import neatlogic.framework.exception.auth.NoAuthGroupException;
import neatlogic.framework.license.LicenseManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

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
                AuthBase authIns = c.newInstance();
                authMap.put(authIns.getAuthName(), authIns);
                if (ModuleUtil.getModuleGroup(authIns.getAuthGroup()) == null) {
                    throw new NoAuthGroupException(authIns.getAuthGroup());
                }
                if (authGroupMap.containsKey(authIns.getAuthGroup())) {
                    authGroupMap.get(authIns.getAuthGroup()).add(authIns);
                } else {
                    List<AuthBase> authList = new ArrayList<>();
                    authList.add(authIns);
                    authGroupMap.put(authIns.getAuthGroup(), authList);
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
        //过滤出对应租户license 权限
        Map<String, List<AuthBase>> licenseAuthGroupMap = new HashMap<>();
        LicenseVo licenseVo = TenantContext.get().getLicenseVo();
        if (licenseVo.getAllAuthGroup() == null) {
            List<String> licenseAuth = LicenseManager.tenantLicenseAuthListMap.get(TenantContext.get().getTenantUuid());
            if(CollectionUtils.isNotEmpty(licenseAuth)) {
                for (Map.Entry<String, List<AuthBase>> authGroupEntry : authGroupMap.entrySet()) {
                    List<AuthBase> authBaseList = authGroupEntry.getValue();
                    List<AuthBase> licenseAuthBaseList = new ArrayList<>();
                    authBaseList.forEach(authBase -> {
                        if (licenseAuth.stream().anyMatch(u -> Objects.equals(u.toUpperCase(Locale.ROOT), authBase.getAuthName()))) {
                            licenseAuthBaseList.add(authBase);
                        }
                    });
                    licenseAuthGroupMap.put(authGroupEntry.getKey(), licenseAuthBaseList);
                }
            }
        }else{
            licenseAuthGroupMap = authGroupMap;
        }
        return licenseAuthGroupMap;
    }
}

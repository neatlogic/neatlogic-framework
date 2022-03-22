/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dto.UserAuthVo;
import codedriver.framework.dto.license.LicenseVo;
import codedriver.framework.exception.auth.NoAuthGroupException;
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
        Reflections reflections = new Reflections("codedriver");
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

    public static Map<String, List<AuthBase>> getAuthGroupMap() {
        //过滤出对应租户license 权限
        Map<String, List<AuthBase>> licenseAuthGroupMap = new HashMap<>();
        LicenseVo licenseVo = TenantContext.get().getLicenseVo();
        if(!licenseVo.getHasAllAuth()) {
            List<UserAuthVo> licenseUserAuth = AuthActionChecker.getAuthListByLicenseAuth(licenseVo);
            for (Map.Entry<String, List<AuthBase>> authGroupEntry : authGroupMap.entrySet()) {
                List<AuthBase> authBaseList = authGroupEntry.getValue();
                List<AuthBase> licenseAuthBaseList = new ArrayList<>();
                authBaseList.forEach(authBase -> {
                    if (licenseUserAuth.stream().anyMatch(u -> Objects.equals(u.getAuth(), authBase.getAuthName()))) {
                        licenseAuthBaseList.add(authBase);
                    }
                });
                licenseAuthGroupMap.put(authGroupEntry.getKey(), licenseAuthBaseList);
            }
        }else{
            licenseAuthGroupMap = authGroupMap;
        }
        return licenseAuthGroupMap;
    }
}

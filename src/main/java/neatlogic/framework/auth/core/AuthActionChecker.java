/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.auth.core;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.init.MaintenanceMode;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.UserAuthVo;
import neatlogic.framework.dto.license.LicenseVo;
import neatlogic.framework.exception.core.LicenseInvalidException;
import neatlogic.framework.license.LicenseManager;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RootComponent
public class AuthActionChecker {

    private static UserMapper userMapper;

    @Resource
    public void setUserMapper(UserMapper _userMapper) {
        userMapper = _userMapper;
    }

    @SafeVarargs
    public static Boolean check(Class<? extends AuthBase>... actionClass) {
        if (actionClass == null || actionClass.length == 0) {
            return false;
        }
        UserContext userContext = UserContext.get();
        List<String> actionList = new ArrayList<>();
        for (Class<? extends AuthBase> action : actionClass) {
            actionList.add(action.getSimpleName());
        }
        if (userContext != null) {
            return checkByUserUuid(userContext.getUserUuid(), actionList);
        } else {
            return false;
        }
    }

    public static Boolean check(String... action) {
        if (action == null || action.length == 0) {
            return false;
        }
        UserContext userContext = UserContext.get();
        List<String> actionList = new ArrayList<>(Arrays.asList(action));
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
        //维护模式下且是维护用户 || ,指定权限无需鉴权
        if (Config.ENABLE_SUPERADMIN() && userUuid.equals(Config.SUPERADMIN()) && MaintenanceMode.maintenanceAuthSet.containsAll(actionList)) {
            return true;
        }
        //系统用户无需鉴权
        if (SystemUser.SYSTEM.getUserUuid().equals(userUuid)) {
            return true;
        }
        //超级管理员无需鉴权
        if (UserContext.get() != null && UserContext.get().getIsSuperAdmin()) {
            return true;
        }

        //豁免license认证则无需判断license是否还有该权限
        if (RequestContext.get() == null || !RequestContext.get().getIsExemptLicense()) {
            //先判断租户license 是否有该auth
            LicenseVo licenseVo = TenantContext.get().getLicenseVo();
            if (licenseVo == null) {
                throw new LicenseInvalidException();
            }
            if (licenseVo.getAllAuthGroup() == null) {
                List<String> licenseAuthList = LicenseManager.tenantLicenseAuthListMap.get(TenantContext.get().getTenantUuid());
                if (CollectionUtils.isNotEmpty(licenseAuthList)) {
                    actionList = actionList.stream().filter(o -> licenseAuthList.stream().anyMatch(l -> Objects.equals(l.toUpperCase(Locale.ROOT), o))).collect(Collectors.toList());
                }
            }
            if (CollectionUtils.isEmpty(actionList)) {
                return false;
            }
        }
        //判断从数据库查询的用户权限是否满足
        List<UserAuthVo> userAuthVoList = userMapper.searchUserAllAuthByUserAuthCache(new UserAuthVo(userUuid));
        List<String> userAuthList = userAuthVoList.stream().map(UserAuthVo::getAuth).collect(Collectors.toList());
        List<String> contains = userAuthList.stream().filter(actionList::contains).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(contains)) {
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
                getUserAuthListByAuth(authBase, userAuthList);
            }
        }
    }


    /**
     * 递归穿透获取权限
     *
     * @param authBase     权限对象
     * @param userAuthList 用户对应权限
     */
    public static void getUserAuthListByAuth(AuthBase authBase, List<UserAuthVo> userAuthList) {
        if (authBase != null) {
            List<Class<? extends AuthBase>> authClassList = authBase.getIncludeAuths();
            for (Class<? extends AuthBase> authClass : authClassList) {
                if (userAuthList.stream().noneMatch(o -> Objects.equals(o.getAuth(), authClass.getSimpleName()))) {//防止回环
                    AuthBase auth = AuthFactory.getAuthInstance(authClass.getSimpleName());
                    userAuthList.add(new UserAuthVo(auth));
                    getUserAuthListByAuth(auth, userAuthList);
                }
            }
        }
    }

    /**
     * 递归穿透获取权限
     *
     * @param authBase 权限对象
     * @param authList 权限
     */
    public static void getAuthListByAuth(AuthBase authBase, List<String> authList) {
        if (authBase != null) {
            List<Class<? extends AuthBase>> authClassList = authBase.getIncludeAuths();
            for (Class<? extends AuthBase> authClass : authClassList) {
                if (authList.stream().noneMatch(o -> Objects.equals(o, authClass.getSimpleName()))) {//防止回环
                    AuthBase auth = AuthFactory.getAuthInstance(authClass.getSimpleName());
                    authList.add(auth.getAuthName());
                    getAuthListByAuth(auth, authList);
                }
            }
        }
    }
}

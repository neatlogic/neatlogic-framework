/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.init;

import codedriver.framework.auth.core.AuthBase;
import codedriver.framework.auth.core.AuthFactory;
import codedriver.framework.auth.label.AUTHORITY_MODIFY;
import codedriver.framework.auth.label.ROLE_MODIFY;
import codedriver.framework.auth.label.TEAM_MODIFY;
import codedriver.framework.auth.label.USER_MODIFY;
import codedriver.framework.common.config.Config;
import codedriver.framework.dto.UserAuthVo;
import codedriver.framework.dto.UserVo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Title: MaintenanceMode
 * @Package codedriver.framework.auth.init
 * @Description:
 * 维护模式下（config文件 is.maintenance.mode = true）
 * administrator 厂商维护用户可拥有 用户、角色、组织架构、权限的维护权限。上生产后请关闭维护模式 即设置为false
 * @Author: 89770
 * @Date: 2021/1/5 14:50
 **/
public class MaintenanceMode {
    public static final Set<String> maintenanceAuthSet = new HashSet<>();

    static {
        maintenanceAuthSet.add(USER_MODIFY.class.getSimpleName());
        maintenanceAuthSet.add(TEAM_MODIFY.class.getSimpleName());
        maintenanceAuthSet.add(ROLE_MODIFY.class.getSimpleName());
        maintenanceAuthSet.add(AUTHORITY_MODIFY.class.getSimpleName());
    }

    /**
     * @Description: 获取厂商维护人员
     * @Author: 89770
     * @Date: 2021/1/5 15:06
     * @Params: []
     * @Returns: codedriver.framework.dto.UserVo
     **/
    public static UserVo getMaintenanceUser(){
        UserVo userVo = new UserVo();
        userVo.setUuid(Config.SUPERADMIN());
        userVo.setUserId(Config.SUPERADMIN());
        userVo.setUserName("厂商维护人员");
        userVo.setIsActive(1);
        userVo.setVipLevel(0);
        List<UserAuthVo> authList = new ArrayList<>();
        for (String auth :maintenanceAuthSet){
            AuthBase authVo = AuthFactory.getAuthInstance(auth);
            UserAuthVo userAuthVo = new UserAuthVo();
            userAuthVo.setAuth(auth);
            userAuthVo.setAuthGroup(authVo.getAuthGroup());
            authList.add(userAuthVo);
        }
        userVo.setUserAuthList(authList);
        return userVo;
    }


}

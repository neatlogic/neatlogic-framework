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

package neatlogic.framework.auth.init;

import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.auth.core.AuthFactory;
import neatlogic.framework.auth.label.AUTHORITY_MODIFY;
import neatlogic.framework.auth.label.ROLE_MODIFY;
import neatlogic.framework.auth.label.TEAM_MODIFY;
import neatlogic.framework.auth.label.USER_MODIFY;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.UserAuthVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.util.I18nUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Title: MaintenanceMode
 * @Package neatlogic.framework.auth.init
 * @Description: 维护模式下-DenableNoSecret=true
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
     * @Returns: neatlogic.framework.dto.UserVo
     **/
    public static UserVo getMaintenanceUser() {
        UserVo userVo = new UserVo();
        userVo.setUuid(Config.MAINTENANCE());
        userVo.setUserId(Config.MAINTENANCE());
        userVo.setUserName(I18nUtils.getStaticMessage("nfai.maintenancemode.getmaintenanceuser"));
        userVo.setIsActive(1);
        userVo.setIsDelete(0);
        userVo.setVipLevel(0);
        List<UserAuthVo> authList = new ArrayList<>();
        for (String auth : maintenanceAuthSet) {
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

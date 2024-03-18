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

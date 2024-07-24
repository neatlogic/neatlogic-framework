/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.usersession;

import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dto.RoleTeamVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.util.List;

@Component
public class UserSessionListener {
    @Resource
    TeamMapper teamMapper;
    @Resource
    RoleMapper roleMapper;

    @TransactionalEventListener
    public void handleRoleTeamEvent(UserSessionRefreshByRoleTeamEvent event) {
        // 事务提交后执行的逻辑
        List<RoleTeamVo> roleTeamVos = event.getRoleTeamList();
        if(CollectionUtils.isNotEmpty(roleTeamVos)){

        }
        System.out.println("事务提交后执行的逻辑");
    }

    @TransactionalEventListener
    public void handleTeamEvent(UserSessionRefreshByTeamEvent event) {
        // 事务提交后执行的逻辑
        event.getTeamList();
        System.out.println("事务提交后执行的逻辑");
    }

    @TransactionalEventListener
    public void handleUserEvent(UserSessionRefreshByUserEvent event) {
        // 事务提交后执行的逻辑
        event.getUserUuidList();
        System.out.println("事务提交后执行的逻辑");
    }
}

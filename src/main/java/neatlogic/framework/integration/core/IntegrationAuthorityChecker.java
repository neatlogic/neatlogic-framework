/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.integration.core;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthActionChecker;
import neatlogic.framework.auth.label.INTEGRATION_MODIFY;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import org.apache.commons.lang3.StringUtils;

/** 共用原集成执行授权规则，目录发现和执行入口使用当前用户的同一授权范围。 */
public final class IntegrationAuthorityChecker {
    /** 共用校验工具不创建独立服务状态。 */
    private IntegrationAuthorityChecker() { }

    /** 管理权限或原集成执行授权命中时允许使用，不接受客户端替代身份。 */
    public static boolean canExecute(String integrationUuid, IntegrationMapper mapper) {
        UserContext user = UserContext.get();
        if (user == null || StringUtils.isBlank(user.getUserUuid())) {
            return false;
        }
        return AuthActionChecker.check(INTEGRATION_MODIFY.class)
                || mapper.checkUserHasIntegrationAuthority(integrationUuid, "execute", user.getUserUuid(),
                user.getTeamUuidList(), user.getRoleUuidList()) > 0;
    }

    /** 复用原业务权限异常，拒绝未获授权的集成访问。 */
    public static void checkExecute(String integrationUuid, IntegrationMapper mapper) throws PermissionDeniedException {
        if (!canExecute(integrationUuid, mapper)) {
            throw new PermissionDeniedException();
        }
    }
}

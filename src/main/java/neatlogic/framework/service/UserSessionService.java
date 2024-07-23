/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserSessionService {

    /**
     * 删除用户会话
     *
     * @param userUuidList 用户uuid(内存表不支持事务)
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void deleteUserSessionByUserUuid(List<String> userUuidList);

    /**
     * 更新用户会话
     *
     * @param tokenHash    token hash
     * @param authInfoHash 认证hash
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateUserSessionAuthInfoHashByTokenHash(String tokenHash, String authInfoHash);
}

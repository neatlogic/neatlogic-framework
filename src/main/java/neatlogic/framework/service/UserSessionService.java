/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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

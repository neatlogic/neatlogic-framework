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

package neatlogic.module.framework.auditconfig.handler;

import neatlogic.framework.auditconfig.core.AuditCleanerBase;
import neatlogic.framework.dao.mapper.LoginMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LoginAuditCleaner extends AuditCleanerBase {

    @Resource
    private LoginMapper loginMapper;

    @Override
    protected void myClean(int dayBefore) throws Exception {
        loginMapper.deleteLoginAuditByDayBefore(dayBefore);
    }

    @Override
    public String getName() {
        return "LOGIN-AUDIT";
    }
}

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

package neatlogic.framework.restful.audit;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.restful.dao.mapper.ApiAuditMapper;
import neatlogic.framework.restful.dto.ApiAuditVo;
import neatlogic.framework.util.AuditUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Deprecated
public class ApiAuditSaveThread extends NeatLogicThread {

    private static ApiAuditMapper apiAuditMapper;

    @Autowired
    public void setApiMapper(ApiAuditMapper _apiAuditManager) {
        apiAuditMapper = _apiAuditManager;
    }

    private ApiAuditVo apiAuditVo;

    public ApiAuditSaveThread(ApiAuditVo _apiAuditVo) {
        super("API-AUDIT-SAVER");
        apiAuditVo = _apiAuditVo;
    }

    public ApiAuditSaveThread() {
        super("API-AUDIT-SAVER");
    }

    @Override
    protected void execute() {
        if (apiAuditVo != null) {
            AuditUtil.saveAuditDetail(apiAuditVo, "api_audit");
            apiAuditMapper.insertApiAudit(apiAuditVo);
        }
    }

}

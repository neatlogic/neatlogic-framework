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

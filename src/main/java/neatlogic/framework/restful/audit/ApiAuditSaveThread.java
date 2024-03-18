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

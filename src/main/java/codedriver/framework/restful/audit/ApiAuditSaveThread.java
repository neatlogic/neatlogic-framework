/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.audit;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.restful.dao.mapper.ApiAuditMapper;
import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.util.AuditUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiAuditSaveThread extends CodeDriverThread {

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

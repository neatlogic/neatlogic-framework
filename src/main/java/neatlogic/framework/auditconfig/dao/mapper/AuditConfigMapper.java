/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.auditconfig.dao.mapper;

import neatlogic.framework.auditconfig.dto.AuditConfigVo;

import java.util.List;

public interface AuditConfigMapper {
    List<AuditConfigVo> searchAuditConfig();

    AuditConfigVo getAuditConfigByName(String name);

    void saveAuditConfig(AuditConfigVo auditConfigVo);

    void deleteAuditConfig(String name);
}

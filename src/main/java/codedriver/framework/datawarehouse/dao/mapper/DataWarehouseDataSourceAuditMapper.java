/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.dao.mapper;

import codedriver.framework.datawarehouse.dto.ReportDataSourceAuditVo;

import java.util.List;

public interface DataWarehouseDataSourceAuditMapper {
    int searchReportDataSourceAuditCount(ReportDataSourceAuditVo reportDataSourceAuditVo);

    List<ReportDataSourceAuditVo> searchReportDataSourceAudit(ReportDataSourceAuditVo reportDataSourceAuditVo);

    void insertReportDataSourceAudit(ReportDataSourceAuditVo reportDataSourceAuditVo);

    void updateReportDataSourceAudit(ReportDataSourceAuditVo reportDataSourceAuditVo);

    void deleteReportDataSourceAuditByDatasourceId(Long datasourceId);
}

/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.service;

import codedriver.framework.datawarehouse.dto.DataSourceVo;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

public interface ReportDataSourceService {
    void deleteReportDataSource(DataSourceVo reportDataSourceVo);

    @Transactional
    void executeReportDataSource(DataSourceVo dataSourceVo) throws SQLException;
}

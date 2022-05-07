/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.dao.mapper;

import codedriver.framework.datawarehouse.dto.ReportDataSourceConditionVo;
import codedriver.framework.datawarehouse.dto.ReportDataSourceFieldVo;
import codedriver.framework.datawarehouse.dto.ReportDataSourceVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataWarehouseDataSourceMapper {
    List<ReportDataSourceVo> getAllHasCronReportDataSource();

    int checkDataSourceNameIsExists(ReportDataSourceVo reportDataSourceVo);

    List<ReportDataSourceVo> getReportDataSourceByIdList(@Param("idList") List<Long> dataSourceIdList);

    ReportDataSourceVo getReportDataSourceById(Long id);

    List<ReportDataSourceVo> searchReportDataSource(ReportDataSourceVo reportDataSourceVo);

    int searchReportDataSourceCount(ReportDataSourceVo reportDataSourceVo);

    void insertReportDataSource(ReportDataSourceVo reportDataSourceVo);

    void insertReportDataSourceField(ReportDataSourceFieldVo reportDataSourceFieldVo);

    void insertReportDataSourceCondition(ReportDataSourceConditionVo reportDataSourceConditionVo);

    void updateReportDataSource(ReportDataSourceVo reportDataSourceVo);

    void updateReportDataSourceIsActive(ReportDataSourceVo reportDataSourceVo);

    void updateReportDataSourceDataCount(ReportDataSourceVo reportDataSourceVo);

    void updateReportDataSourceStatus(ReportDataSourceVo reportDataSourceVo);

    void updateReportDataSourceConditionValue(ReportDataSourceConditionVo reportDataSourceConditionVo);

    void resetReportDataSourceStatus();

    void deleteReportDataSourceById(Long id);

    void deleteReportDataSourceConditionByDataSourceId(Long dataSourceId);

    void deleteReportDataSourceFieldByDataSourceId(Long dataSourceId);
}

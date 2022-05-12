/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.dao.mapper;

import codedriver.framework.datawarehouse.dto.DataSourceConditionVo;
import codedriver.framework.datawarehouse.dto.DataSourceFieldVo;
import codedriver.framework.datawarehouse.dto.DataSourceVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataWarehouseDataSourceMapper {
    List<DataSourceVo> getAllHasCronReportDataSource();

    int checkDataSourceNameIsExists(DataSourceVo reportDataSourceVo);

    List<DataSourceVo> getDataSourceByIdList(@Param("idList") List<Long> dataSourceIdList);

    DataSourceVo getDataSourceById(Long id);

    List<DataSourceVo> searchDataSource(DataSourceVo reportDataSourceVo);

    int searchDataSourceCount(DataSourceVo reportDataSourceVo);

    void insertDataSource(DataSourceVo reportDataSourceVo);

    void insertDataSourceField(DataSourceFieldVo reportDataSourceFieldVo);

    // void insertReportDataSourceCondition(DataSourceConditionVo reportDataSourceConditionVo);

    void updateDataSourceField(DataSourceFieldVo dataSourceFieldVo);

    void updateDataSourceFieldCondition(DataSourceFieldVo dataSourceFieldVo);

    void updateDataSource(DataSourceVo dataSourceVo);

    void updateDataSourcePolicy(DataSourceVo dataSourceVo);

    void updateReportDataSourceIsActive(DataSourceVo reportDataSourceVo);

    void updateReportDataSourceDataCount(DataSourceVo reportDataSourceVo);

    void updateReportDataSourceStatus(DataSourceVo reportDataSourceVo);

    void updateReportDataSourceConditionValue(DataSourceConditionVo reportDataSourceConditionVo);

    void resetReportDataSourceStatus();

    void deleteReportDataSourceById(Long id);

    //void deleteReportDataSourceConditionByDataSourceId(Long dataSourceId);

    void deleteDataSourceFieldByDataSourceId(Long dataSourceId);

    void deleteDataSourceFieldById(Long id);
}

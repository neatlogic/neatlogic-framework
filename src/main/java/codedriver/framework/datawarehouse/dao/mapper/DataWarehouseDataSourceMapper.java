/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.dao.mapper;

import codedriver.framework.datawarehouse.dto.DataSourceParamVo;
import codedriver.framework.datawarehouse.dto.DataSourceFieldVo;
import codedriver.framework.datawarehouse.dto.DataSourceVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataWarehouseDataSourceMapper {
    List<DataSourceVo> getAllHasCronReportDataSource();

    int checkDataSourceNameIsExists(DataSourceVo dataSourceVo);

    List<DataSourceVo> getDataSourceByIdList(@Param("idList") List<Long> dataSourceIdList);

    DataSourceVo getDataSourceById(Long id);

    DataSourceVo getDataSourceDetailByName(String name);

    List<DataSourceVo> searchDataSource(DataSourceVo dataSourceVo);

    int searchDataSourceCount(DataSourceVo reportDataSourceVo);

    List<Long> getExistIdListByIdList(List<Long> idList);

    DataSourceVo getDataSourceNameAndFieldNameListById(Long id);

    List<DataSourceVo> getDataSourceNameListByIdList(List<Long> idList);

    List<DataSourceVo> getDataSourceNameListByNameList(List<String> nameList);

    List<DataSourceFieldVo> getDataSourceFieldNameListByIdList(List<Long> idList);

    List<DataSourceFieldVo> getDataSourceFieldNameListByDatasourceIdAndNameList(@Param("datasourceId") Long datasourceId, @Param("nameList") List<String> nameList);

    void insertDataSource(DataSourceVo reportDataSourceVo);

    void insertDataSourceParam(DataSourceParamVo dataSourceParamVo);

    void insertDataSourceField(DataSourceFieldVo dataSourceFieldVo);

    // void insertReportDataSourceCondition(DataSourceConditionVo reportDataSourceConditionVo);

    void updateDataSourceField(DataSourceFieldVo dataSourceFieldVo);

    void updateDataSourceParam(DataSourceParamVo dataSourceParamVo);

    void updateDataSourceParamCurrentValue(DataSourceParamVo dataSourceParamVo);

    void updateDataSourceFieldCondition(DataSourceFieldVo dataSourceFieldVo);

    void updateDataSource(DataSourceVo dataSourceVo);

    void updateDataSourcePolicy(DataSourceVo dataSourceVo);

    void updateReportDataSourceIsActive(DataSourceVo dataSourceVo);

    void updateReportDataSourceDataCount(DataSourceVo dataSourceVo);

    void updateReportDataSourceStatus(DataSourceVo dataSourceVo);

    void updateReportDataSourceConditionValue(DataSourceParamVo dataSourceConditionVo);

    void resetReportDataSourceStatus();

    void deleteReportDataSourceById(Long id);

    //void deleteReportDataSourceConditionByDataSourceId(Long dataSourceId);

    void deleteDataSourceFieldByDataSourceId(Long dataSourceId);

    void deleteDataSourceFieldById(Long id);

    void deleteDataSourceParamById(Long id);
}

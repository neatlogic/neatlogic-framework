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

package neatlogic.framework.datawarehouse.dao.mapper;

import neatlogic.framework.datawarehouse.dto.DataSourceParamVo;
import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataWarehouseDataSourceMapper {
    List<DataSourceVo> getAllHasCronReportDataSource();

    int checkDataSourceNameIsExists(DataSourceVo dataSourceVo);

    List<DataSourceVo> getDataSourceByIdList(@Param("idList") List<Long> dataSourceIdList);

    DataSourceVo getDataSourceById(Long id);

    List<DataSourceVo> getAllDataSource();

    DataSourceVo getDataSourceDetailByName(String name);

    List<DataSourceVo> searchDataSource(DataSourceVo dataSourceVo);

    int searchDataSourceCount(DataSourceVo reportDataSourceVo);

    List<Long> getExistIdListByIdList(List<Long> idList);

    DataSourceVo getDataSourceNameAndFieldNameListById(Long id);

    List<DataSourceVo> getDataSourceListByNameList(List<String> nameList);

    void insertDataSource(DataSourceVo reportDataSourceVo);

    void insertDataSourceParam(DataSourceParamVo dataSourceParamVo);

    void batchInsertDataSourceParam(List<DataSourceParamVo> list);

    void insertDataSourceField(DataSourceFieldVo dataSourceFieldVo);

    void batchInsertDataSourceField(List<DataSourceFieldVo> list);

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

    void deleteDataSourceFieldByIdList(List<Long> idList);

    void deleteDataSourceParamById(Long id);

    void deleteDataSourceParamByIdList(List<Long> idList);

    void deleteDataSourceParamByDataSourceId(Long dataSourceId);
}

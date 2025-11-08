/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.datawarehouse.dao.mapper;

import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceParamVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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

    void updateDataSourceJobTimeById(DataSourceVo dataSourceVo);

    void updateDataSourceNextFireTimeById(@Param("id") Long id, @Param("nextFireTime") Date nextFireTime);

    void deleteReportDataSourceById(Long id);

    //void deleteReportDataSourceConditionByDataSourceId(Long dataSourceId);

    void deleteDataSourceFieldByDataSourceId(Long dataSourceId);

    void deleteDataSourceFieldById(Long id);

    void deleteDataSourceFieldByIdList(List<Long> idList);

    void deleteDataSourceParamById(Long id);

    void deleteDataSourceParamByIdList(List<Long> idList);

    void deleteDataSourceParamByDataSourceId(Long dataSourceId);
}

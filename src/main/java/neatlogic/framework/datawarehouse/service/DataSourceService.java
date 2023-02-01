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

package neatlogic.framework.datawarehouse.service;

import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface DataSourceService {
    void deleteReportDataSource(DataSourceVo reportDataSourceVo);

    @Transactional
    void executeReportDataSource(DataSourceVo dataSourceVo) throws SQLException;

    /**
     * 新增数据源
     *
     * @param vo 数据源vo
     */
    void insertDataSource(DataSourceVo vo);

    /**
     * 更新数据源
     *
     * @param newVo        新数据源vo
     * @param newXmlConfig 新数据源xml配置
     * @param oldVo        旧数据源vo
     */
    void updateDataSource(DataSourceVo newVo, DataSourceVo newXmlConfig, DataSourceVo oldVo);

    /**
     * 创建数据源动态表
     *
     * @param dataSourceVo 数据源vo
     */
    void createDataSourceSchema(DataSourceVo dataSourceVo);

    /**
     * 加载或卸载报表数据源数据同步定时作业
     *
     * @param dataSourceVo 数据源vo
     */
    void loadOrUnloadReportDataSourceJob(DataSourceVo dataSourceVo);

    /**
     * 将旧数据源的条件设置还原到新数据源的条件列表
     *
     * @param newFieldList 新条件列表
     * @param oldFieldList 旧条件列表
     * @return 还原后的新条件列表
     */
    List<DataSourceFieldVo> revertFieldCondition(List<DataSourceFieldVo> newFieldList, List<DataSourceFieldVo> oldFieldList);
}

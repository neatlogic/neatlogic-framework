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

package neatlogic.framework.datawarehouse.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.datawarehouse.dto.DataSourceDataVo;
import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface DataSourceService {
    /** 查询完整数据源详情，不存在时返回 null，字段投影由调用方按需处理。 */
    DataSourceVo getDataSourceById(Long id);

    /** 查询数据源列表，保留筛选、分页及 defaultValue 优先转换为 ID 列表的回显行为。 */
    JSONObject searchDataSource(DataSourceVo dataSourceVo);

    /** 查询动态数据，保留过滤、排序、过期和 limit 三态及原表头、分页响应结构。 */
    JSONObject searchDataSourceData(DataSourceDataVo dataVo);

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

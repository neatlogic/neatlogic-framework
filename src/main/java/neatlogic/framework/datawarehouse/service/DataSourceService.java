/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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

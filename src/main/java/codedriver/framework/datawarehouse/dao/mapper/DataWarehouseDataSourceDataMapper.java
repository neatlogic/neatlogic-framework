/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.dao.mapper;

import codedriver.framework.datawarehouse.dto.DataSourceDataVo;
import codedriver.framework.datawarehouse.dto.DataSourceVo;

import java.util.HashMap;
import java.util.List;

public interface DataWarehouseDataSourceDataMapper {
    int getDataSourceDataCount(DataSourceDataVo reportDataSourceDataVo);

    HashMap<String, Object> getAggregateFieldValue(DataSourceDataVo dataSourceDataVo);

    int searchDataSourceDataCount(DataSourceDataVo reportDataSourceDataVo);

    List<HashMap<String, Object>> searchDataSourceData(DataSourceDataVo dataSourceDataVo);

    void insertDataSourceData(DataSourceDataVo reportDataSourceDataVo);

    void truncateTable(DataSourceVo reportDataSourceVo);

    //需要返回删除行数
    int clearExpiredData(DataSourceDataVo reportDataSourceDataVo);
}

package neatlogic.framework.dashboard.datasource.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;

/** 大屏数据源扩展入口，各模块通过 Spring Bean 提供自身类型的查询实现。 */
public interface IDashboardDatasourceHandler {

    /** 返回唯一的数据源类型标识，注册和查找均保留非空值的原始内容。 */
    String getType();

    /** 查询数据源列表，保留搜索、分页和选中值回显参数。 */
    JSONObject listDatasource(JSONObject params);

    /** 查询数据源详情及字段定义，保留既有 DataSourceVo 返回约定。 */
    DataSourceVo getDatasource(JSONObject params);

    /** 查询组件动态数据，保留过滤、排序及行数限制参数。 */
    JSONObject searchDatasourceData(JSONObject params);
}

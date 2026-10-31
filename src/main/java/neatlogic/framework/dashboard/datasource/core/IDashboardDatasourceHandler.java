package neatlogic.framework.dashboard.datasource.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.exception.type.PermissionDeniedException;

/** 大屏数据源扩展入口，各模块通过 Spring Bean 提供自身类型的查询实现。 */
public interface IDashboardDatasourceHandler {

    /** 返回唯一的数据源类型标识，注册和查找均保留非空值的原始内容。 */
    String getType();

    /** 按当前请求语言返回类型展示名称，每次查询时解析，不缓存翻译结果。 */
    String getTypeName();

    /** 查询数据源列表，保留搜索、分页和选中值回显参数。 */
    JSONObject listDatasource(JSONObject params);

    /**
     * 查询数据源字段定义，返回仅包含 fieldList 数组的 JSON 对象，数据源不存在时返回 null。
     * 每项为仅包含 id、label、type、isCondition、inputType、config 六键的 JSON 对象，config 保留原对象。
     */
    JSONObject getDatasource(JSONObject params);

    /** 查询组件动态数据，保留过滤、排序及行数限制参数；原样抛出来源执行权限异常，由接口框架处理。 */
    JSONObject searchDatasourceData(JSONObject params) throws PermissionDeniedException;
}

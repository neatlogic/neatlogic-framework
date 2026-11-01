package neatlogic.module.framework.dashboard.datasource.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dashboard.datasource.core.IDashboardDatasourceHandler;
import neatlogic.framework.datawarehouse.dto.DataSourceDataVo;
import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.datawarehouse.service.DataSourceService;
import neatlogic.framework.util.$;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 数据仓库大屏数据源处理器，通过公共服务复用原查询契约。
 * 仅实现本类型的查询，数据源类型解析和分发由公共工厂负责。
 */
@Service
public class DatawarehouseDashboardDatasourceHandler implements IDashboardDatasourceHandler {
    @Resource
    private DataSourceService dataSourceService;

    /** 工厂根据固定类型标识注册处理器，后续类型可提供独立实现。 */
    @Override
    public String getType() {
        return "datawarehouse";
    }

    /** 按当前请求语言展示数据源类型名称，保持类型标识用于查询分发。 */
    @Override
    public String getTypeName() {
        return $.t("nmfddh.datawarehousedashboarddatasourcehandler.gettypename");
    }

    /** 详情只返回组件所需的字段 JSON，数据源 ID 不存在时仍返回 null。 */
    @Override
    public JSONObject getDatasource(JSONObject params) {
        DataSourceVo dataSourceVo = dataSourceService.getDataSourceById(params.getLong("id"));
        if (dataSourceVo == null) {
            return null;
        }
        JSONArray fieldList = new JSONArray();
        if (CollectionUtils.isNotEmpty(dataSourceVo.getFieldList())) {
            for (DataSourceFieldVo fieldVo : dataSourceVo.getFieldList()) {
                // 显式映射六个公共属性，通过 getter 保留标签回退、默认输入类型和配置解析行为。
                JSONObject field = new JSONObject();
                field.put("id", fieldVo.getId());
                field.put("label", fieldVo.getLabel());
                field.put("type", fieldVo.getType());
                field.put("isCondition", fieldVo.getIsCondition());
                field.put("inputType", fieldVo.getInputType());
                field.put("config", fieldVo.getConfig());
                fieldList.add(field);
            }
        }
        JSONObject result = new JSONObject();
        result.put("fieldList", fieldList);
        return result;
    }

    /** 将请求转换为原 DTO 并委托公共服务，保留列表筛选、回显和分页契约。 */
    @Override
    public JSONObject listDatasource(JSONObject params) {
        return dataSourceService.searchDataSource(JSON.toJavaObject(params, DataSourceVo.class));
    }

    /** 动态取数直接返回公共服务结果，过滤、排序和 limit 三态统一由服务及原 SQL 处理。 */
    @Override
    public JSONObject searchDatasourceData(JSONObject params) {
        return dataSourceService.searchDataSourceData(JSON.toJavaObject(params, DataSourceDataVo.class));
    }
}

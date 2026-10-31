package neatlogic.module.framework.dashboard.datasource.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dashboard.datasource.core.IDashboardDatasourceHandler;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceDataMapper;
import neatlogic.framework.datawarehouse.dao.mapper.DataWarehouseDataSourceMapper;
import neatlogic.framework.datawarehouse.dto.DataSourceDataVo;
import neatlogic.framework.datawarehouse.dto.DataSourceFieldVo;
import neatlogic.framework.datawarehouse.dto.DataSourceVo;
import neatlogic.framework.datawarehouse.exceptions.DataSourceIsNotFoundException;
import neatlogic.framework.util.$;
import neatlogic.framework.util.TableResultUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 数据仓库大屏数据源处理器，直接复用公共 Mapper 和原查询契约。
 * 仅实现本类型的查询，数据源类型解析和分发由公共工厂负责。
 */
@Service
public class DatawarehouseDashboardDatasourceHandler implements IDashboardDatasourceHandler {
    @Resource
    private DataWarehouseDataSourceMapper dataSourceMapper;
    @Resource
    private DataWarehouseDataSourceDataMapper dataSourceDataMapper;

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
        DataSourceVo dataSourceVo = dataSourceMapper.getDataSourceById(params.getLong("id"));
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

    /** 保留原列表接口的精确回显、筛选和分页响应，不增加默认启用状态过滤。 */
    @Override
    public JSONObject listDatasource(JSONObject params) {
        DataSourceVo dataSourceVo = JSON.toJavaObject(params, DataSourceVo.class);
        if (CollectionUtils.isNotEmpty(dataSourceVo.getDefaultValue())) {
            List<Long> idList = new ArrayList<>();
            for (int i = 0; i < dataSourceVo.getDefaultValue().size(); i++) {
                idList.add(dataSourceVo.getDefaultValue().getLongValue(i));
            }
            dataSourceVo.setIdList(idList);
        }
        List<DataSourceVo> dataSourceList = dataSourceMapper.searchDataSource(dataSourceVo);
        if (CollectionUtils.isNotEmpty(dataSourceList)) {
            dataSourceVo.setRowNum(dataSourceMapper.searchDataSourceCount(dataSourceVo));
        }
        return TableResultUtil.getResult(dataSourceList, dataSourceVo);
    }

    /**
     * 保留原取数接口的字段别名、元数据和空结果语义。
     * limit 不补默认值：缺省分页、0 不限、正数限行，由既有 MyBatis SQL 处理。
     */
    @Override
    public JSONObject searchDatasourceData(JSONObject params) {
        DataSourceDataVo dataVo = JSON.toJavaObject(params, DataSourceDataVo.class);
        DataSourceVo dataSourceVo = dataSourceMapper.getDataSourceById(dataVo.getDataSourceId());
        if (dataSourceVo == null) {
            throw new DataSourceIsNotFoundException(dataVo.getDataSourceId());
        }
        JSONObject result = new JSONObject();
        if (CollectionUtils.isNotEmpty(dataSourceVo.getFieldList())) {
            dataVo.setFieldList(dataSourceVo.getFieldList());
            dataVo.setRowNum(dataSourceDataMapper.searchDataSourceDataCount(dataVo));
            List<HashMap<String, Object>> resultList = dataSourceDataMapper.searchDataSourceData(dataVo);
            JSONArray headerList = new JSONArray();
            for (DataSourceFieldVo fieldVo : dataSourceVo.getFieldList()) {
                JSONObject head = new JSONObject();
                head.put("key", "field_" + fieldVo.getId());
                head.put("name", fieldVo.getName());
                head.put("title", fieldVo.getLabel());
                headerList.add(head);
            }
            // 系统时间列保持原键名，文案归属 framework，组件仍通过 name/field_ID 完成映射。
            JSONObject insertTimeHead = new JSONObject();
            insertTimeHead.put("key", "insertTime");
            insertTimeHead.put("title", $.t("nmfddh.datawarehousedashboarddatasourcehandler.header.inserttime"));
            insertTimeHead.put("type", "time");
            headerList.add(insertTimeHead);
            JSONObject expireTimeHead = new JSONObject();
            expireTimeHead.put("key", "expireTime");
            expireTimeHead.put("title", $.t("nmfddh.datawarehousedashboarddatasourcehandler.header.expiretime"));
            expireTimeHead.put("type", "time");
            headerList.add(expireTimeHead);
            result.put("currentPage", dataVo.getCurrentPage());
            result.put("pageSize", dataVo.getPageSize());
            result.put("pageCount", dataVo.getPageCount());
            result.put("rowNum", dataVo.getRowNum());
            result.put("theadList", headerList);
            result.put("tbodyList", resultList);
        }
        return result;
    }
}

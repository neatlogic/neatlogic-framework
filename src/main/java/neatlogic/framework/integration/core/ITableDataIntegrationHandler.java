package neatlogic.framework.integration.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.integration.dto.IntegrationVo;

/**
 * 可选的表格取数扩展，允许模块规范复用集成接口而不依赖旧表单的主键和分页约定。
 */
public interface ITableDataIntegrationHandler {

    /** 按处理器自身规范取数，返回现有的 theadList、tbodyList 结构。 */
    JSONObject searchTableData(IntegrationVo integrationVo, JSONObject paramObj);
}

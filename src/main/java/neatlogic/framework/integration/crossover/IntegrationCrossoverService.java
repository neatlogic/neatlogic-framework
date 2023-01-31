/*
 * Copyright(c) 2022 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.integration.crossover;

import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.table.ColumnVo;
import neatlogic.framework.integration.dto.table.SourceColumnVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author linbq
 * @since 2022/1/18 16:01
 **/
public interface IntegrationCrossoverService extends ICrossoverService {

    List<ColumnVo> getColumnList(IntegrationVo integrationVo);

    JSONArray getTheadList(IntegrationVo integrationVo, List<String> columnList);

    List<Map<String, Object>> getTbodyList(IntegrationResultVo resultVo, List<String> columnList);
    /**
     * 集成属性数据查询
     * @param jsonObj 参数
     * @return
     */
    JSONObject searchTableData(JSONObject jsonObj);

    /**
     * 合并filterList和sourceColumnList
     * @param filterList
     * @param sourceColumnList
     */
    boolean mergeFilterListAndSourceColumnList(JSONArray filterList, List<SourceColumnVo> sourceColumnList);
}

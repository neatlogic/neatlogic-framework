/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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

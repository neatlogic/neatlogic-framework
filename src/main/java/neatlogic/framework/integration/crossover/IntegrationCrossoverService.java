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

package neatlogic.framework.integration.crossover;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.table.ColumnVo;
import neatlogic.framework.integration.dto.table.SourceColumnVo;

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

    /**
     * 找出允许搜索的输入参数名称列表
     * @param integrationVo
     * @return
     */
    List<String> getSearchAbleInputParamNameList(IntegrationVo integrationVo);
}

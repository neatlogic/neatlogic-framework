/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
}

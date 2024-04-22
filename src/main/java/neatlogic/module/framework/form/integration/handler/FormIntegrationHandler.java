/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.module.framework.form.integration.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.integration.IntegrationRequestResultFieldNotExistsException;
import neatlogic.framework.exception.integration.IntegrationRequestResultFormatInvalidException;
import neatlogic.framework.exception.integration.IntegrationRequestResultIsEmptyException;
import neatlogic.framework.integration.core.IntegrationHandlerBase;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.PatternVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author linbq
 * @since 2021/11/23 11:44
 **/
@Deprecated
public class FormIntegrationHandler extends IntegrationHandlerBase {
    @Override
    public String getName() {
        return "表单外部数据源查询";
    }

    @Override
    public Integer hasPattern() {
        return 1;
    }

    @Override
    public List<PatternVo> getInputPattern() {
        List<PatternVo> jsonList = new ArrayList<>();
        jsonList.add(new PatternVo("keyword", "input", ApiParamType.STRING, 0, "关键字"));
        jsonList.add(new PatternVo("defaultValue", "input", ApiParamType.JSONARRAY, 0, "唯一标识数组，用于回显数据"));
        jsonList.add(new PatternVo("currentPage", "input", ApiParamType.INTEGER, 0, "当前页"));
        jsonList.add(new PatternVo("pageSize", "input", ApiParamType.INTEGER, 0, "每页大小"));
        jsonList.add(new PatternVo("needPage", "input", ApiParamType.BOOLEAN, 0, "是否分页"));
        PatternVo sourceColumnList = new PatternVo("sourceColumnList", "input", ApiParamType.JSONARRAY, 0, "过滤参数列表");
        sourceColumnList.addChild(new PatternVo("column", "input", ApiParamType.STRING, 0, "过滤参数名称"));
        sourceColumnList.addChild(new PatternVo("expression", "input", ApiParamType.STRING, 0, "表达式"));
        sourceColumnList.addChild(new PatternVo("value", "input", ApiParamType.STRING, 0, "过滤参数值"));
        jsonList.add(sourceColumnList);
        return jsonList;
    }

    @Override
    public List<PatternVo> getOutputPattern() {
        List<PatternVo> jsonList = new ArrayList<>();
        PatternVo theadList = new PatternVo("theadList", "output", ApiParamType.JSONARRAY, 0, "表头列表");
        theadList.addChild(new PatternVo("key", "output", ApiParamType.STRING, 0, "表头键值"));
        theadList.addChild(new PatternVo("title", "output", ApiParamType.STRING, 0, "表头名称"));
        theadList.addChild(new PatternVo("isSearchable", "output", ApiParamType.INTEGER, 0, "该字段是否可以搜索过滤"));
        jsonList.add(theadList);
        PatternVo tbodyList = new PatternVo("tbodyList", "output", ApiParamType.JSONARRAY, 0, "数据列表");
        jsonList.add(tbodyList);
        jsonList.add(new PatternVo("currentPage", "output", ApiParamType.INTEGER, 0, "当前页"));
        jsonList.add(new PatternVo("rowNum", "output", ApiParamType.INTEGER, 0, "条目数量"));
        jsonList.add(new PatternVo("pageSize", "output", ApiParamType.INTEGER, 0, "每页大小"));
        jsonList.add(new PatternVo("pageCount", "output", ApiParamType.INTEGER, 0, "页数"));
        return jsonList;
    }

    @Override
    public void validate(IntegrationResultVo resultVo) throws ApiRuntimeException {
        if (StringUtils.isBlank(resultVo.getError())) {
            if (StringUtils.isNotBlank(resultVo.getTransformedResult())) {
                JSONObject transformedResult;
                try {
                    transformedResult = JSONObject.parseObject(resultVo.getTransformedResult());
                } catch (Exception ex) {
                    throw new IntegrationRequestResultFormatInvalidException();
                }
                if (MapUtils.isNotEmpty(transformedResult)) {
                    Set<String> keys = transformedResult.keySet();
                    List<String> outputFieldList = getOutputPattern().stream().map(PatternVo::getName).collect(Collectors.toList());
                    List<String> fieldList = ListUtils.removeAll(outputFieldList, keys);
                    if (CollectionUtils.isNotEmpty(fieldList)) {
                        throw new IntegrationRequestResultFieldNotExistsException(fieldList);
                    }
                    JSONArray theadList = transformedResult.getJSONArray("theadList");
                    if (CollectionUtils.isNotEmpty(theadList)) {
                        for (int i = 0; i < theadList.size(); i++) {
                            JSONObject threadObj = theadList.getJSONObject(i);
                            fieldList = new ArrayList<>();
                            if (!threadObj.containsKey("key")) {
                                fieldList.add("theadList[" + i + "].key" );
                            }
                            if (!threadObj.containsKey("title")) {
                                fieldList.add("theadList[" + i + "].title" );
                            }
                            if (CollectionUtils.isNotEmpty(fieldList)) {
                                throw new IntegrationRequestResultFieldNotExistsException(fieldList);
                            }
                        }
                    } else {
                        throw new IntegrationRequestResultFieldNotExistsException("theadList");
                    }
                } else {
                    throw new IntegrationRequestResultIsEmptyException();
                }
            } else {
                throw new IntegrationRequestResultIsEmptyException();
            }
        }
    }

    @Override
    protected void beforeSend(IntegrationVo integrationVo) {

    }

    @Override
    protected void afterReturn(IntegrationVo integrationVo) {

    }
}

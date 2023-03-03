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

package neatlogic.module.framework.form.attribute.handler;

import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.attribute.core.IFormAttributeHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.module.framework.form.service.FormService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CheckboxHandler extends FormHandlerBase {

    @Resource
    private FormService formService;

    @Override
    public String getHandler() {
        return FormHandler.FORMCHECKBOX.getHandler();
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public int getSort() {
        return 6;
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        if (model == FormConditionModel.CUSTOM) {
            return "select";
        }
        return "checkbox";
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        List<String> valueList = JSON.parseArray(JSON.toJSONString(attributeDataVo.getDataObj()), String.class);
        if (CollectionUtils.isNotEmpty(valueList)) {
            return getTextOrValue(configObj, valueList, ConversionType.TOTEXT.getValue());
        } else {
            return valueList;
        }
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return valueConversionText(attributeDataVo, configObj);
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        Object value = formService.textConversionValueForSelectHandler(text, config);
        if (value == null) {
            return null;
        }
        if (value instanceof List) {
            return value;
        }
        List<Object> list = new ArrayList<>();
        list.add(value);
        return list;
//        Object result = null;
//        if (CollectionUtils.isNotEmpty(values)) {
//            result = getTextOrValue(config, values, ConversionType.TOVALUE.getValue());
//        }
//        return result;
    }

    private Object getTextOrValue(JSONObject configObj, List<String> valueList, String conversionType) {
        List<String> result = new ArrayList<>();
        String dataSource = configObj.getString("dataSource");
        if ("static".equals(dataSource)) {
            Map<Object, String> valueTextMap = new HashMap<>();
            List<ValueTextVo> dataList =
                    JSON.parseArray(JSON.toJSONString(configObj.getJSONArray("dataList")), ValueTextVo.class);
            if (CollectionUtils.isNotEmpty(dataList)) {
                for (ValueTextVo data : dataList) {
                    valueTextMap.put(data.getValue(), data.getText());
                }
            }
            if (ConversionType.TOTEXT.getValue().equals(conversionType)) {
                for (String value : valueList) {
                    String text = valueTextMap.get(value);
                    if (text != null) {
                        result.add(text);
                    } else {
                        result.add(value);
                    }
                }
            } else if (ConversionType.TOVALUE.getValue().equals(conversionType)) {
                for (String value : valueList) {
                    result.add(valueTextMap.get(value));
                }
            }
        } else if ("matrix".equals(dataSource)) {// 其他，如动态数据源
            if (ConversionType.TOTEXT.getValue().equals(conversionType)) {
                for (String value : valueList) {
                    if (value.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
                        result.add(value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1]);
                    } else {
                        result.add(value);
                    }
                }
            } else if (ConversionType.TOVALUE.getValue().equals(conversionType)) {
                String matrixUuid = configObj.getString("matrixUuid");
                ValueTextVo mapping = JSON.toJavaObject(configObj.getJSONObject("mapping"), ValueTextVo.class);
                if (StringUtils.isNotBlank(matrixUuid) && CollectionUtils.isNotEmpty(valueList)
                        && mapping != null) {
                    for (String value : valueList) {
                        result.add(getValue(matrixUuid, mapping, value));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMCHECKBOX.getHandlerName();
    }

    @Override
    public String getIcon() {
        return "ts-check-square-o";
    }

    @Override
    public ParamType getParamType() {
        return ParamType.ARRAY;
    }

    @Override
    public String getDataType() {
        return "string";
    }

    @Override
    public boolean isConditionable() {
        return true;
    }

    @Override
    public boolean isShowable() {
        return true;
    }

    @Override
    public boolean isValueable() {
        return true;
    }

    @Override
    public boolean isFilterable() {
        return true;
    }

    @Override
    public boolean isExtendable() {
        return false;
    }

    @Override
    public String getModule() {
        return "framework";
    }

    @Override
    public boolean isForTemplate() {
        return true;
    }

    @Override
    public boolean isProcessTaskBatchSubmissionTemplateParam() {
        return true;
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    @Override
    protected List<String> myIndexFieldContentList(String data) {
        if (data.startsWith("[") && data.endsWith("]")) {
            JSONArray jsonArray = JSONArray.parseArray(data);
            return JSONObject.parseArray(jsonArray.toJSONString(), String.class);
        }
        return null;
    }

    @Override
    public Boolean isNeedSliceWord() {
        return false;
    }

    //表单组件配置信息
//{
//	"handler": "formcheckbox",
//	"label": "复选框_7",
//	"type": "form",
//	"uuid": "4120a16ca0dc4b90a794eea80d3b61d3",
//	"config": {
//		"isRequired": false,
//		"mapping": {
//			"text": "",
//			"value": ""
//		},
//		"defaultValueList": [
//			"1"
//		],
//		"ruleList": [],
//		"validList": [],
//		"isMultiple": true,
//		"quoteUuid": "",
//		"dataList": [
//			{
//				"text": "复选1",
//				"value": "1"
//			},
//			{
//				"text": "复选2",
//				"value": "2"
//			}
//		],
//		"width": "100%",
//		"defaultValueType": "self",
//		"authorityConfig": [
//			"common#alluser"
//		],
//		"dataSource": "static",
//		"direction": "transverse"
//	}
//}
    //保存数据结构
//    ["1"]
    //返回数据结构
//{
//	"textList": [
//		"复选1"
//	],
//	"valueList": [
//		"1"
//	]
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        JSONArray valueArray = (JSONArray) attributeDataVo.getDataObj();
        if (CollectionUtils.isNotEmpty(valueArray)) {
            List<String> textList = new ArrayList<>();
            List<String> valueList = valueArray.toJavaList(String.class);
            String dataSource = configObj.getString("dataSource");
            if ("static".equals(dataSource)) {
                Map<Object, String> valueTextMap = new HashMap<>();
                JSONArray dataArray = configObj.getJSONArray("dataList");
                if (CollectionUtils.isNotEmpty(dataArray)) {
                    List<ValueTextVo> dataList = dataArray.toJavaList(ValueTextVo.class);
                    if (CollectionUtils.isNotEmpty(dataList)) {
                        for (ValueTextVo data : dataList) {
                            valueTextMap.put(data.getValue(), data.getText());
                        }
                    }
                }

                for (String value : valueList) {
                    String text = valueTextMap.get(value);
                    if (text != null) {
                        textList.add(text);
                    } else {
                        textList.add(value);
                    }
                }
            } else if ("matrix".equals(dataSource)) {// 其他，如动态数据源
                for (String value : valueList) {
                    if (value.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
                        textList.add(value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1]);
                    } else {
                        textList.add(value);
                    }
                }
            }
            resultObj.put("textList", textList);
        }
        resultObj.put("valueList", valueArray);
        return resultObj;
    }

    @Override
    public void makeupFormAttribute(FormAttributeVo formAttributeVo) {
        Set<String> matrixUuidSet = new HashSet<>();
        Map<String, Set<String>> matrixUuidAttributeUuidSetMap = new HashMap<>();
        JSONObject config = formAttributeVo.getConfigObj();
        String dataSource = config.getString("dataSource");
        if ("matrix".equals(dataSource)) {
            String matrixUuid = config.getString("matrixUuid");
            if (StringUtils.isNotBlank(matrixUuid)) {
                matrixUuidSet.add(matrixUuid);
                Set<String> attributeUuidSet = new HashSet<>();
                /** 字段映射 **/
                JSONObject mapping = config.getJSONObject("mapping");
                if (MapUtils.isNotEmpty(mapping)) {
                    String value = mapping.getString("value");
                    if (StringUtils.isNotBlank(value)) {
                        attributeUuidSet.add(value);
                    }
                    String text = mapping.getString("text");
                    if (StringUtils.isNotBlank(text)) {
                        attributeUuidSet.add(text);
                    }
                }
                /** 过滤条件 **/
                JSONArray sourceColumnList = config.getJSONArray("sourceColumnList");
                if (CollectionUtils.isNotEmpty(sourceColumnList)) {
                    for (int i = 0; i < sourceColumnList.size(); i++) {
                        JSONObject sourceColumnObj = sourceColumnList.getJSONObject(i);
                        if (MapUtils.isNotEmpty(sourceColumnObj)) {
                            String column = sourceColumnObj.getString("column");
                            if (StringUtils.isNotBlank(column)) {
                                attributeUuidSet.add(column);
                            }
                        }
                    }
                }
                matrixUuidAttributeUuidSetMap.put(matrixUuid, attributeUuidSet);
            }
        }
        formAttributeVo.setMatrixUuidSet(matrixUuidSet);
        formAttributeVo.setMatrixUuidAttributeUuidSetMap(matrixUuidAttributeUuidSetMap);
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject detailedData = getMyDetailedData(attributeDataVo, configObj);
        if (detailedData != null) {
            JSONArray textList = detailedData.getJSONArray("textList");
            if (CollectionUtils.isNotEmpty(textList)) {
                return String.join(",", textList.toJavaList(String.class));
            }
        }
        return null;
    }
}

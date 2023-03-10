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
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.attribute.core.IFormAttributeHandler;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.matrix.core.IMatrixDataSourceHandler;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerFactory;
import neatlogic.framework.matrix.dto.MatrixColumnVo;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import neatlogic.framework.matrix.dto.MatrixVo;
import neatlogic.framework.matrix.exception.MatrixDataSourceHandlerNotFoundException;
import neatlogic.framework.matrix.exception.MatrixNotFoundException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CascadeHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMCASCADER.getHandler();
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMCASCADER.getHandlerName();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "cascadelist";
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public int getSort() {
        return 4;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            List<String> textList = textArray.toJavaList(String.class);
            return String.join("/", textList);
        }
        JSONArray valueArray = resultObj.getJSONArray("valueList");
        if (CollectionUtils.isNotEmpty(valueArray)) {
            List<String> valueList = valueArray.toJavaList(String.class);
            return String.join("/", valueList);
        }

        return null;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            List<String> textList = textArray.toJavaList(String.class);
            return String.join("/", textList);
        }
        return null;
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return text;
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
            JSONArray dataList = configObj.getJSONArray("dataList");
            for (String value : valueList) {
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject dataObject = dataList.getJSONObject(i);
                    if (ConversionType.TOTEXT.getValue().equals(conversionType) && Objects.equals(dataObject.getString("value"), value)) {
                        result.add(dataObject.getString("text"));
                        dataList = dataObject.getJSONArray("children");
                        break;
                    } else if (ConversionType.TOVALUE.getValue().equals(conversionType) && Objects.equals(dataObject.getString("text"), value)) {
                        result.add(dataObject.getString("value"));
                        dataList = dataObject.getJSONArray("children");
                        break;
                    }
                }
            }
        } else if ("matrix".equals(dataSource)) {// ???????????????????????????
            String matrixUuid = configObj.getString("matrixUuid");
            List<ValueTextVo> mappingList =
                    JSON.parseArray(JSON.toJSONString(configObj.getJSONArray("mapping")), ValueTextVo.class);
            if (StringUtils.isNotBlank(matrixUuid) && CollectionUtils.isNotEmpty(valueList)
                    && CollectionUtils.isNotEmpty(mappingList)) {
                if (valueList.size() > 0 && mappingList.size() > 0) {
                    if (ConversionType.TOTEXT.getValue().equals(conversionType)) {
                        List<MatrixColumnVo> sourceColumnList = new ArrayList<>();
                        result.add(getText(matrixUuid, mappingList.get(0), valueList.get(0), sourceColumnList));
                        if (valueList.size() > 1 && mappingList.size() > 1) {
                            result.add(getText(matrixUuid, mappingList.get(1), valueList.get(1), sourceColumnList));
                            if (valueList.size() > 2 && mappingList.size() > 2) {
                                result.add(getText(matrixUuid, mappingList.get(2), valueList.get(2), sourceColumnList));
                            }
                        }
                    } else if (ConversionType.TOVALUE.getValue().equals(conversionType)) {
                        result.add(getValueForCascade(matrixUuid, mappingList.get(0), valueList.get(0)));
                        if (valueList.size() > 1 && mappingList.size() > 1) {
                            result.add(getValueForCascade(matrixUuid, mappingList.get(1), valueList.get(1)));
                            if (valueList.size() > 2 && mappingList.size() > 2) {
                                result.add(getValueForCascade(matrixUuid, mappingList.get(2), valueList.get(2)));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private String getText(String matrixUuid, ValueTextVo mapping, String value, List<MatrixColumnVo> sourceColumnList) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
        try {
            MatrixVo matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
            if (matrixVo == null) {
                throw new MatrixNotFoundException(matrixUuid);
            }
            IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrixVo.getType());
            if (matrixDataSourceHandler == null) {
                throw new MatrixDataSourceHandlerNotFoundException(matrixVo.getType());
            }
            MatrixDataVo dataVo = new MatrixDataVo();
            dataVo.setMatrixUuid(matrixUuid);
            List<String> columnList = new ArrayList<>();
            columnList.add((String) mapping.getValue());
            columnList.add(mapping.getText());
            dataVo.setColumnList(columnList);
            sourceColumnList.add(new MatrixColumnVo((String) mapping.getValue(), split[0]));
            sourceColumnList.add(new MatrixColumnVo(mapping.getText(), split[1]));
            dataVo.setSourceColumnList(sourceColumnList);
            List<Map<String, JSONObject>> tbodyList = matrixDataSourceHandler.searchTableColumnData(dataVo);
            for (Map<String, JSONObject> firstObj : tbodyList) {
                JSONObject textObj = firstObj.get(mapping.getText());
                if (Objects.equals(textObj.getString("value"), split[1])) {
                    return textObj.getString("text");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return split[1];
    }

    private String getValueForCascade(String matrixUuid, ValueTextVo mapping, String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        try {
            MatrixVo matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
            if (matrixVo == null) {
                throw new MatrixNotFoundException(matrixUuid);
            }
            IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrixVo.getType());
            if (matrixDataSourceHandler == null) {
                throw new MatrixDataSourceHandlerNotFoundException(matrixVo.getType());
            }
            MatrixDataVo dataVo = new MatrixDataVo();
            dataVo.setMatrixUuid(matrixUuid);
            List<String> columnList = new ArrayList<>();
            columnList.add((String) mapping.getValue());
            columnList.add(mapping.getText());
            dataVo.setColumnList(columnList);
            List<Map<String, JSONObject>> tbodyList = matrixDataSourceHandler.searchTableColumnData(dataVo);
            for (Map<String, JSONObject> firstObj : tbodyList) {
                JSONObject valueObj = firstObj.get(mapping.getValue());
                if (valueObj.getString("compose").contains(value)) {
                    JSONObject textObj = firstObj.get(mapping.getText());
                    return valueObj.getString("value") + IFormAttributeHandler.SELECT_COMPOSE_JOINER + textObj.getString("value");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getIcon() {
        return "tsfont-formcascadelist";
    }

    @Override
    public ParamType getParamType() {
        return ParamType.STRING;
    }

    @Override
    public String getDataType() {
        return "string";
    }

    @Override
    public boolean isConditionable() {
        return false;
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
        return false;
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    //????????????????????????
//{
//	"handler": "formcascadelist",
//	"label": "????????????_10",
//	"type": "form",
//	"uuid": "2d14af78bf684e36837251df7481fa2c",
//	"config": {
//		"isRequired": false,
//		"mapping": [
//			{
//				"text": "",
//				"value": ""
//			},
//			{
//				"text": "",
//				"value": ""
//			}
//		],
//		"defaultValueList": [
//			"1",
//			"3"
//		],
//		"ruleList": [],
//		"validList": [],
//		"quoteUuid": "",
//		"levelType": "2",
//		"dataList": [
//			{
//				"children": [
//					{
//						"index": 2,
//						"text": "??????1",
//						"value": "3"
//					},
//					{
//						"index": 2,
//						"text": "??????2",
//						"value": "4"
//					}
//				],
//				"index": 1,
//				"text": "??????1",
//				"value": "1"
//			},
//			{
//				"children": [
//					{
//						"index": 2,
//						"text": "??????3",
//						"value": "5"
//					},
//					{
//						"index": 2,
//						"text": "??????4",
//						"value": "6"
//					}
//				],
//				"index": 1,
//				"text": "??????2",
//				"value": "2"
//			}
//		],
//		"width": "100%",
//		"defaultValueType": "self",
//		"placeholder": [
//			"?????????????????????",
//			"?????????????????????",
//			"?????????????????????"
//		],
//		"authorityConfig": [
//			"common#alluser"
//		],
//		"dataSource": "static",
//		"direction": "transverse"
//	}
//}
    //??????????????????
//    ["1","3"]
    //??????????????????
//{
//	"textList": [
//		"??????1",
//		"??????1"
//	],
//	"valueList": [
//		"1",
//		"3"
//	]
//}
    /*
    {
        "handler": "formcascader",
        "reaction": {
            "filter": {},
            "hide": {},
            "readonly": {},
            "setvalue": {},
            "disable": {},
            "display": {},
            "emit": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-formcascadelist",
        "hasValue": true,
        "label": "???????????????_4",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "mapping": {
                "text": "",
                "value": ""
            },
            "description": "",
            "matrixUuid": "",
            "isHide": false,
            "isMask": false,
            "isReadOnly": false,
            "levelType": 2,
            "dataList": [
                {
                    "children": [
                        {
                            "text": "b71",
                            "value": "71"
                        },
                        {
                            "text": "b72",
                            "value": "72"
                        },
                        {
                            "text": "b73",
                            "value": "73"
                        }
                    ],
                    "text": "a7",
                    "value": "7"
                },
                {
                    "children": [
                        {
                            "text": "b81",
                            "value": "81"
                        },
                        {
                            "text": "b82",
                            "value": "82"
                        },
                        {
                            "text": "b83",
                            "value": "83"
                        }
                    ],
                    "text": "a8",
                    "value": "8"
                },
                {
                    "children": [
                        {
                            "text": "a91",
                            "value": "91"
                        },
                        {
                            "text": "a92",
                            "value": "92"
                        },
                        {
                            "text": "a93",
                            "value": "93"
                        }
                    ],
                    "text": "a9",
                    "value": "9"
                }
            ],
            "width": "100%",
            "isDisabled": false,
            "defaultValueType": "self",
            "dataSource": "static"
        },
        "uuid": "998c8ff9d83b48ee8c56a83de562cb23"
    }
     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj == null) {
            return resultObj;
        }
        JSONArray valueArray = (JSONArray) dataObj;
        resultObj.put("valueList", valueArray);
        if (CollectionUtils.isEmpty(valueArray)) {
            return resultObj;
        }
        List<String> valueList = valueArray.toJavaList(String.class);
        List<String> textList = new ArrayList<>();
        String dataSource = configObj.getString("dataSource");
        if ("static".equals(dataSource)) {
            JSONArray dataList = configObj.getJSONArray("dataList");
            for (String value : valueList) {
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject dataObject = dataList.getJSONObject(i);
                    if (Objects.equals(dataObject.getString("value"), value)) {
                        textList.add(dataObject.getString("text"));
                        dataList = dataObject.getJSONArray("children");
                        break;
                    }
                }
            }
        }
//        else if ("matrix".equals(dataSource)) {// ???????????????????????????
//            for (String value : valueList) {
//                if (value.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
//                    textList.add(value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1]);
//                } else {
//                    textList.add(value);
//                }
//            }
//        }
        resultObj.put("textList", textList);
        return resultObj;
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

    @Override
    public void makeupFormAttribute(FormAttributeVo formAttributeVo) {
        Set<String> matrixUuidSet = new HashSet<>();
        Map<String, Set<String>> matrixUuidAttributeUuidSetMap = new HashMap<>();
        JSONObject config = formAttributeVo.getConfigObj();
        String dataSource = config.getString("dataSource");
        if ("matrix".equals(dataSource)) {
            String matrixUuid = config.getString("matrixUuid");
            if (StringUtils.isNotBlank(matrixUuid)) {
                Set<String> attributeUuidSet = new HashSet<>();
                matrixUuidSet.add(matrixUuid);
                /** ???????????? **/
                JSONArray mappingArray = config.getJSONArray("mapping");
                if (CollectionUtils.isNotEmpty(mappingArray)) {
                    for (int i = 0; i < mappingArray.size(); i++) {
                        JSONObject mapping = mappingArray.getJSONObject(i);
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
                    }
                }
                matrixUuidAttributeUuidSetMap.put(matrixUuid, attributeUuidSet);
            }
        }
        formAttributeVo.setMatrixUuidSet(matrixUuidSet);
        formAttributeVo.setMatrixUuidAttributeUuidSetMap(matrixUuidAttributeUuidSetMap);
    }
}

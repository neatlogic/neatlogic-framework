/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class SelectHandler extends FormHandlerBase {

    @Resource
    private FormService formService;

    @Override
    public String getHandler() {
        return FormHandler.FORMSELECT.getHandler();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "select";
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMSELECT.getHandlerName();
    }

    @Override
    public String getIcon() {
        return "tsfont-formselect";
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public int getSort() {
        return 3;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            return textArray;
        }
        return resultObj.getJSONArray("valueList");
//        Object dataObj = attributeDataVo.getDataObj();
//        if (dataObj != null) {
//            boolean isMultiple = configObj.getBooleanValue("isMultiple");
//            attributeDataVo.setIsMultiple(isMultiple ? 1 : 0);
//            String dataSource = configObj.getString("dataSource");
//            if ("static".equals(dataSource)) {
//                List<ValueTextVo> dataList =
//                        JSON.parseArray(JSON.toJSONString(configObj.getJSONArray("dataList")), ValueTextVo.class);
//                if (CollectionUtils.isNotEmpty(dataList)) {
//                    Map<Object, String> valueTextMap = new HashMap<>();
//                    for (ValueTextVo data : dataList) {
//                        valueTextMap.put(data.getValue(), data.getText());
//                    }
//                    if (isMultiple) {
//                        List<String> valueList = JSON.parseArray(JSON.toJSONString(dataObj), String.class);
//                        if (CollectionUtils.isNotEmpty(valueList)) {
//                            List<String> textList = new ArrayList<>();
//                            for (String key : valueList) {
//                                String text = valueTextMap.get(key);
//                                if (text != null) {
//                                    textList.add(text);
//                                } else {
//                                    textList.add(key);
//                                }
//                            }
//                            return textList;
//                        }
//                        return valueList;
//                    } else {
//                        if (dataObj instanceof JSONArray) {
//                            return ((JSONArray) dataObj).getString(0);
//                        } else {
//                            String text = valueTextMap.get(dataObj);
//                            if (text != null) {
//                                return text;
//                            } else {
//                                return dataObj;
//                            }
//                        }
//                    }
//                }
//            } else {// ???????????????????????????
//                if (isMultiple) {
//                    List<String> valueList = JSON.parseArray(JSON.toJSONString(dataObj), String.class);
//                    if (CollectionUtils.isNotEmpty(valueList)) {
//                        List<String> textList = new ArrayList<>();
//                        for (String key : valueList) {
//                            if (key.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
//                                textList.add(key.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1]);
//                            } else {
//                                textList.add(key);
//                            }
//                        }
//                        return textList;
//                    }
//                    return valueList;
//                } else {
//                    if (dataObj instanceof JSONArray) {
//                        JSONArray dl = (JSONArray) dataObj;
//                        if (dl.size() > 0) {
//                            String value = dl.getString(0);
//                            if (value.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
//                                return value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1];
//                            } else {
//                                return value;
//                            }
//                        }
//                    } else {
//                        String value = (String) dataObj;
//                        if (value.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
//                            return value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1];
//                        } else {
//                            return dataObj;
//                        }
//                    }
//                }
//            }
//        }
//        return dataObj;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            List<String> textList = textArray.toJavaList(String.class);
            return String.join("???", textList);
        }
        return null;
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
        boolean isMultiple = config.getBooleanValue("isMultiple");
        if (isMultiple) {
            List<Object> list = new ArrayList<>();
            list.add(value);
            return list;
        }
        return value;
//        Object result = null;
//        if (CollectionUtils.isNotEmpty(values)) {
//            boolean isMultiple = config.getBooleanValue("isMultiple");
//            String dataSource = config.getString("dataSource");
//            if ("static".equals(dataSource)) {
//                List<ValueTextVo> dataList =
//                        JSON.parseArray(JSON.toJSONString(config.getJSONArray("dataList")), ValueTextVo.class);
//                if (CollectionUtils.isNotEmpty(dataList)) {
//                    Map<String, Object> valueTextMap = new HashMap<>();
//                    for (ValueTextVo data : dataList) {
//                        valueTextMap.put(data.getText(), data.getValue());
//                    }
//                    if (isMultiple) {
//                        JSONArray jsonArray = new JSONArray();
//                        for (String value : values) {
//                            jsonArray.add(valueTextMap.get(value));
//                        }
//                        result = jsonArray;
//                    } else {
//                        result = valueTextMap.get(values.get(0));
//                    }
//                }
//
//            } else if ("matrix".equals(dataSource)) {
//                String matrixUuid = config.getString("matrixUuid");
//                ValueTextVo mapping = JSON.toJavaObject(config.getJSONObject("mapping"), ValueTextVo.class);
//                if (StringUtils.isNotBlank(matrixUuid) && CollectionUtils.isNotEmpty(values)
//                        && mapping != null) {
//                    if (isMultiple) {
//                        JSONArray jsonArray = new JSONArray();
//                        for (String value : values) {
//                            jsonArray.add(getValue(matrixUuid, mapping, value));
//                        }
//                        result = jsonArray;
//                    } else {
//                        result = getValue(matrixUuid, mapping, values.get(0));
//                    }
//                }
//            }
//        }
//        return result;
    }

    @Override
    public ParamType getParamType() {
        return ParamType.ARRAY;
    }

    @Override
    public String getDataType() {
        return "list";
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
        List<String> contentList = new ArrayList<>();
        if (data.startsWith("[") && data.endsWith("]")) {
            JSONArray jsonArray = JSONArray.parseArray(data);
            for (Object obj : jsonArray) {
                if (obj != null) {
                    contentList.addAll(Arrays.asList(obj.toString().split("&=&")));
                }
            }
            return JSONObject.parseArray(jsonArray.toJSONString(), String.class);
        } else {
            contentList.addAll(Arrays.asList(data.split("&=&")));
        }
        return contentList;
    }

    @Override
    public Boolean isNeedSliceWord() {
        return false;
    }

    //????????????????????????
//{
//	"handler": "formselect",
//	"label": "?????????_8",
//	"type": "form",
//	"uuid": "4425ae2a7fd3402ebc828233a79d5c62",
//	"config": {
//		"isRequired": false,
//		"mapping": {
//			"text": "",
//			"value": ""
//		},
//		"defaultValueList": "1",
//		"ruleList": [],
//		"validList": [],
//		"isMultiple": false,
//		"quoteUuid": "",
//		"dataList": [
//			{
//				"text": "??????1",
//				"value": "1"
//			},
//			{
//				"text": "??????2",
//				"value": "2"
//			}
//		],
//		"width": "100%",
//		"defaultValueType": "self",
//		"placeholder": "?????????",
//		"authorityConfig": [
//			"common#alluser"
//		],
//		"dataSource": "static",
//		"direction": "transverse"
//	}
//}
    //??????????????????
//    1
    //??????????????????
//{
//	"textList": [
//		"??????1"
//	],
//	"valueList": [
//		"1"
//	]
//}
    /*
    {
        "handler": "formselect",
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
        "icon": "tsfont-formselect",
        "hasValue": true,
        "label": "?????????_7",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "mapping": {
                "text": "579c9de2986d48738cc0ecafab2f43d3",
                "value": "a22002af151b4da589bc390f3ad164f5"
            },
            "defaultValue": "",
            "description": "",
            "isMultiple": true,
            "matrixUuid": "e54221ef3b814eebbf57df252426923c",
            "isHide": false,
            "isMask": false,
            "isReadOnly": false,
            "sourceColumnList": [],
            "dataList": [],
            "width": "100%",
            "isDisabled": false,
            "defaultValueType": "self",
            "dataSource": "matrix"
        },
        "uuid": "8fb965f2523d4989b875993f502a5581",
        "switchHandler": [
            "formselect",
            "formradio",
            "formcheckbox"
        ]
    }
     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj == null) {
            return resultObj;
        }
        List<String> valueList = new ArrayList<>();
        List<String> textList = new ArrayList<>();
        boolean isMultiple = configObj.getBooleanValue("isMultiple");
        attributeDataVo.setIsMultiple(isMultiple ? 1 : 0);
        String dataSource = configObj.getString("dataSource");
        if ("static".equals(dataSource)) {
            JSONArray dataArray = configObj.getJSONArray("dataList");
            if (CollectionUtils.isEmpty(dataArray)) {
                return resultObj;
            }
            List<ValueTextVo> dataList = dataArray.toJavaList(ValueTextVo.class);
            Map<Object, String> valueTextMap = dataList.stream().collect(Collectors.toMap(e -> e.getValue(), e -> e.getText()));
            if (isMultiple) {
                JSONArray valueArray = (JSONArray) dataObj;
                if (CollectionUtils.isNotEmpty(valueArray)) {
                    valueList = valueArray.toJavaList(String.class);
                    for (String key : valueList) {
                        String text = valueTextMap.get(key);
                        if (text != null) {
                            textList.add(text);
                        } else {
                            textList.add(key);
                        }
                    }
                }
            } else {
                String dataObjStr = dataObj.toString();
                valueList.add(dataObjStr);
                String text = valueTextMap.get(dataObjStr);
                if (text != null) {
                    textList.add(text);
                } else {
                    textList.add(dataObjStr);
                }
            }
        } else {// ???????????????????????????
            if (isMultiple) {
                JSONArray valueArray = (JSONArray) dataObj;
                if (CollectionUtils.isNotEmpty(valueArray)) {
                    valueList = valueArray.toJavaList(String.class);
                    for (String key : valueList) {
                        if (key.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
                            textList.add(key.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1]);
                        } else {
                            textList.add(key);
                        }
                    }
                }
            } else {
                String value = (String) dataObj;
                if (value.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
                    textList.add(value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1]);
                } else {
                    textList.add((String) dataObj);
                }
            }
        }
        resultObj.put("valueList", valueList);
        resultObj.put("textList", textList);
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
                /** ???????????? **/
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
                /** ???????????? **/
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

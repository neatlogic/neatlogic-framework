/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.constvalue.FormHandler;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.dto.FormAttributeVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;
import codedriver.framework.form.attribute.core.IFormAttributeHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SelectHandler extends FormHandlerBase {

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
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj != null) {
            boolean isMultiple = configObj.getBooleanValue("isMultiple");
            attributeDataVo.setIsMultiple(isMultiple ? 1 : 0);
            String dataSource = configObj.getString("dataSource");
            if ("static".equals(dataSource)) {
                List<ValueTextVo> dataList =
                        JSON.parseArray(JSON.toJSONString(configObj.getJSONArray("dataList")), ValueTextVo.class);
                if (CollectionUtils.isNotEmpty(dataList)) {
                    Map<Object, String> valueTextMap = new HashMap<>();
                    for (ValueTextVo data : dataList) {
                        valueTextMap.put(data.getValue(), data.getText());
                    }
                    if (isMultiple) {
                        List<String> valueList = JSON.parseArray(JSON.toJSONString(dataObj), String.class);
                        if (CollectionUtils.isNotEmpty(valueList)) {
                            List<String> textList = new ArrayList<>();
                            for (String key : valueList) {
                                String text = valueTextMap.get(key);
                                if (text != null) {
                                    textList.add(text);
                                } else {
                                    textList.add(key);
                                }
                            }
                            return textList;
                        }
                        return valueList;
                    } else {
                        if (dataObj instanceof JSONArray) {
                            return ((JSONArray) dataObj).getString(0);
                        } else {
                            String text = valueTextMap.get((String) dataObj);
                            if (text != null) {
                                return text;
                            } else {
                                return dataObj;
                            }
                        }
                    }
                }
            } else {// 其他，如动态数据源
                if (isMultiple) {
                    List<String> valueList = JSON.parseArray(JSON.toJSONString(dataObj), String.class);
                    if (CollectionUtils.isNotEmpty(valueList)) {
                        List<String> textList = new ArrayList<>();
                        for (String key : valueList) {
                            if (key.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
                                textList.add(key.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1]);
                            } else {
                                textList.add(key);
                            }
                        }
                        return textList;
                    }
                    return valueList;
                } else {
                    if (dataObj instanceof JSONArray) {
                        JSONArray dl = (JSONArray) dataObj;
                        if (dl.size() > 0) {
                            String value = dl.getString(0);
                            if (value.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
                                return value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1];
                            } else {
                                return value;
                            }
                        }
                    } else {
                        String value = (String) dataObj;
                        if (value.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
                            return value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1];
                        } else {
                            return dataObj;
                        }
                    }
                }
            }
        }
        return dataObj;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return valueConversionText(attributeDataVo, configObj);
    }

    @Override
    public Object textConversionValue(List<String> values, JSONObject config) {
        Object result = null;
        if (CollectionUtils.isNotEmpty(values)) {
            boolean isMultiple = config.getBooleanValue("isMultiple");
            String dataSource = config.getString("dataSource");
            if ("static".equals(dataSource)) {
                List<ValueTextVo> dataList =
                        JSON.parseArray(JSON.toJSONString(config.getJSONArray("dataList")), ValueTextVo.class);
                if (CollectionUtils.isNotEmpty(dataList)) {
                    Map<String, Object> valueTextMap = new HashMap<>();
                    for (ValueTextVo data : dataList) {
                        valueTextMap.put(data.getText(), data.getValue());
                    }
                    if (isMultiple) {
                        JSONArray jsonArray = new JSONArray();
                        for (String value : values) {
                            jsonArray.add(valueTextMap.get(value));
                        }
                        result = jsonArray;
                    } else {
                        result = valueTextMap.get(values.get(0));
                    }
                }

            } else if ("matrix".equals(dataSource)) {
                String matrixUuid = config.getString("matrixUuid");
                ValueTextVo mapping = JSON.toJavaObject(config.getJSONObject("mapping"), ValueTextVo.class);
                if (StringUtils.isNotBlank(matrixUuid) && CollectionUtils.isNotEmpty(values)
                        && mapping != null) {
                    if (isMultiple) {
                        JSONArray jsonArray = new JSONArray();
                        for (String value : values) {
                            jsonArray.add(getValue(matrixUuid, mapping, value));
                        }
                        result = jsonArray;
                    } else {
                        result = getValue(matrixUuid, mapping, values.get(0));
                    }
                }
            }
        }
        return result;
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

    //表单组件配置信息
//{
//	"handler": "formselect",
//	"label": "下拉框_8",
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
//				"text": "下拉1",
//				"value": "1"
//			},
//			{
//				"text": "下拉2",
//				"value": "2"
//			}
//		],
//		"width": "100%",
//		"defaultValueType": "self",
//		"placeholder": "请输入",
//		"authorityConfig": [
//			"common#alluser"
//		],
//		"dataSource": "static",
//		"direction": "transverse"
//	}
//}
    //保存数据结构
//    1
    //返回数据结构
//{
//	"textList": [
//		"下拉1"
//	],
//	"valueList": [
//		"1"
//	]
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj != null) {
            List<String> valueList = new ArrayList<>();
            List<String> textList = new ArrayList<>();
            boolean isMultiple = configObj.getBooleanValue("isMultiple");
            attributeDataVo.setIsMultiple(isMultiple ? 1 : 0);
            String dataSource = configObj.getString("dataSource");
            if ("static".equals(dataSource)) {
                JSONArray dataArray = configObj.getJSONArray("dataList");
                if (CollectionUtils.isNotEmpty(dataArray)) {
                    Map<Object, String> valueTextMap = new HashMap<>();
                    List<ValueTextVo> dataList = dataArray.toJavaList(ValueTextVo.class);
                    for (ValueTextVo data : dataList) {
                        valueTextMap.put(data.getValue(), data.getText());
                    }
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
                        valueList.add((String) dataObj);
                        String text = valueTextMap.get(dataObj);
                        if (text != null) {
                            textList.add(text);
                        } else {
                            textList.add((String) dataObj);
                        }
                    }
                }
            } else {// 其他，如动态数据源
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
        }
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

/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.module.framework.form.attribute.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.util.FormUtil;
import neatlogic.module.framework.form.service.FormService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

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
    public Object conversionDataType(Object source, String attributeLabel) {
        if (source == null) {
            return null;
        }
        if (source instanceof JSONObject) {
            return source;
        } else if (source instanceof JSONArray) {
            return source;
        } else if (source instanceof String) {
            String sourceStr = (String) source;
            if (sourceStr.startsWith("{") && sourceStr.endsWith("}")) {
                try {
                    return JSON.parseObject(sourceStr);
                } catch (Exception e) {
                    throw new AttributeValidException(attributeLabel);
                }
            } else if (sourceStr.startsWith("[") && sourceStr.endsWith("]")) {
                try {
                    return JSON.parseArray(sourceStr);
                } catch (Exception e) {
                    throw new AttributeValidException(attributeLabel);
                }
            } else {
                return source;
            }
        }
        throw new AttributeValidException(attributeLabel);
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
//            } else {// 其他，如动态数据源
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
    public Object getSimpleValue(Object originalValue) {
        return FormUtil.getFormSelectAttributeValueByOriginalValue(originalValue);
    }

    @Override
    public Object getStandardValueBySimpleValue(Object simpleValue, JSONObject configObj) {
        return formService.getSelectStandardValueBySimpleValue(simpleValue, configObj);
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            List<String> textList = textArray.toJavaList(String.class);
            return String.join("、", textList);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        JSONArray list = formService.textConversionValueForSelectHandler(text, config);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        boolean isMultiple = config.getBooleanValue("isMultiple");
        if (isMultiple) {
            return list;
        }
        return list.get(0);
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
        if (data.startsWith("{") && data.endsWith("}")) {
            JSONObject jsonObj = JSON.parseObject(data);
            String value = jsonObj.getString("value");
            contentList.add(value);
//            String text = jsonObj.getString("text");
//            if (StringUtils.isNotBlank(text)) {
//                contentList.add(text);
//            }
        } else if (data.startsWith("[") && data.endsWith("]")) {
            JSONArray jsonArray = JSON.parseArray(data);
            for (Object obj : jsonArray) {
                if (obj != null) {
                    if (obj instanceof JSONObject) {
                        JSONObject jsonObj = (JSONObject) obj;
                        String value = jsonObj.getString("value");
                        contentList.add(value);
//                        String text = jsonObj.getString("text");
//                        if (StringUtils.isNotBlank(text)) {
//                            contentList.add(text);
//                        }
                    } else {
                        contentList.add(obj.toString());
                    }
                }
            }
            return JSON.parseArray(jsonArray.toJSONString(), String.class);
        } else {
            contentList.add(data);
        }
        return contentList;
    }

    @Override
    public Boolean isNeedSliceWord() {
        return false;
    }

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
        "label": "下拉框_7",
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
        return formService.getMyDetailedDataForSelectHandler(attributeDataVo, configObj);
    }

    @Override
    public void makeupFormAttribute(FormAttributeVo formAttributeVo) {
        Set<String> matrixUuidSet = new HashSet<>();
        Map<String, Set<String>> matrixUuidAttributeUuidSetMap = new HashMap<>();
        JSONObject config = formAttributeVo.getConfig();
        String dataSource = config.getString("dataSource");
        if ("matrix".equals(dataSource)) {
            String matrixUuid = config.getString("matrixUuid");
            if (StringUtils.isNotBlank(matrixUuid)) {
                matrixUuidSet.add(matrixUuid);
                Set<String> attributeUuidSet = new HashSet<>();
                /* 字段映射 **/
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
                /* 过滤条件 **/
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

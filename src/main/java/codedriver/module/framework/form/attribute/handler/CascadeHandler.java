/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.dto.FormAttributeVo;
import codedriver.framework.matrix.dto.MatrixColumnVo;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;
import codedriver.framework.form.attribute.core.IFormAttributeHandler;
import codedriver.framework.restful.core.IApiComponent;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentFactory;
import codedriver.framework.restful.dto.ApiVo;
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
        return "formcascadelist";
    }

    @Override
    public String getHandlerName() {
        return "级联下拉";
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
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj != null) {
            List<String> valueList = JSON.parseArray(JSON.toJSONString(dataObj), String.class);
            return getTextOrValue(configObj, valueList, ConversionType.TOTEXT.getValue());
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
            result = getTextOrValue(config, values, ConversionType.TOVALUE.getValue());
        }
        return result;
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
        } else if ("matrix".equals(dataSource)) {// 其他，如动态数据源
            String matrixUuid = configObj.getString("matrixUuid");
            List<ValueTextVo> mappingList =
                    JSON.parseArray(JSON.toJSONString(configObj.getJSONArray("mapping")), ValueTextVo.class);
            if (StringUtils.isNotBlank(matrixUuid) && CollectionUtils.isNotEmpty(valueList)
                    && CollectionUtils.isNotEmpty(mappingList)) {
                ApiVo api = PrivateApiComponentFactory.getApiByToken("matrix/column/data/search/forselect/new");
                if (api != null) {
                    IApiComponent restComponent = PrivateApiComponentFactory.getInstance(api.getHandler());
                    if (restComponent != null) {
                        if (valueList.size() > 0 && mappingList.size() > 0) {
                            if (ConversionType.TOTEXT.getValue().equals(conversionType)) {
                                List<MatrixColumnVo> sourceColumnList = new ArrayList<>();
                                result.add(getText(matrixUuid, mappingList.get(0), valueList.get(0), sourceColumnList, restComponent, api));
                                if (valueList.size() > 1 && mappingList.size() > 1) {
                                    result.add(getText(matrixUuid, mappingList.get(1), valueList.get(1), sourceColumnList, restComponent, api));
                                    if (valueList.size() > 2 && mappingList.size() > 2) {
                                        result.add(getText(matrixUuid, mappingList.get(2), valueList.get(2), sourceColumnList, restComponent, api));
                                    }
                                }
                            } else if (ConversionType.TOVALUE.getValue().equals(conversionType)) {
                                result.add(getValueForCascade(matrixUuid, mappingList.get(0), valueList.get(0), restComponent, api));
                                if (valueList.size() > 1 && mappingList.size() > 1) {
                                    result.add(getValueForCascade(matrixUuid, mappingList.get(1), valueList.get(1), restComponent, api));
                                    if (valueList.size() > 2 && mappingList.size() > 2) {
                                        result.add(getValueForCascade(matrixUuid, mappingList.get(2), valueList.get(2), restComponent, api));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private String getText(String matrixUuid, ValueTextVo mapping, String value, List<MatrixColumnVo> sourceColumnList,
                           IApiComponent restComponent, ApiVo api) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
        try {
            JSONObject paramObj = new JSONObject();
            paramObj.put("matrixUuid", matrixUuid);
            List<String> columnList = new ArrayList<>();
            columnList.add((String) mapping.getValue());
            columnList.add(mapping.getText());
            paramObj.put("columnList", columnList);
            sourceColumnList.add(new MatrixColumnVo((String) mapping.getValue(), split[0]));
            sourceColumnList.add(new MatrixColumnVo(mapping.getText(), split[1]));
            paramObj.put("sourceColumnList", sourceColumnList);
            JSONObject resultObj = (JSONObject) restComponent.doService(api, paramObj, null);
            JSONArray columnDataList = resultObj.getJSONArray("columnDataList");
            for (int i = 0; i < columnDataList.size(); i++) {
                JSONObject firstObj = columnDataList.getJSONObject(i);
                JSONObject textObj = firstObj.getJSONObject(mapping.getText());
                if (Objects.equals(textObj.getString("value"), split[1])) {
                    return textObj.getString("text");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return split[1];
    }

    private String getValueForCascade(String matrixUuid, ValueTextVo mapping, String value, IApiComponent restComponent, ApiVo api) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        try {
            JSONObject paramObj = new JSONObject();
            paramObj.put("matrixUuid", matrixUuid);
            JSONArray columnList = new JSONArray();
            columnList.add((String) mapping.getValue());
            columnList.add(mapping.getText());
            paramObj.put("columnList", columnList);
            JSONObject resultObj = (JSONObject) restComponent.doService(api, paramObj, null);
            JSONArray columnDataList = resultObj.getJSONArray("columnDataList");
            for (int i = 0; i < columnDataList.size(); i++) {
                JSONObject firstObj = columnDataList.getJSONObject(i);
                JSONObject valueObj = firstObj.getJSONObject((String) mapping.getValue());
                if (valueObj.getString("compose").contains(value)) {
                    JSONObject textObj = firstObj.getJSONObject(mapping.getText());
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
                Set<String> attributeUuidSet = new HashSet<>();
                matrixUuidSet.add(matrixUuid);
                /** 字段映射 **/
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
        JSONArray relMatrixUuidArray = config.getJSONArray("relMatrixUuidList");
        if (CollectionUtils.isNotEmpty(relMatrixUuidArray)) {
            List<String> relMatrixUuidList = relMatrixUuidArray.toJavaList(String.class);
            matrixUuidSet.addAll(relMatrixUuidList);
        }
        formAttributeVo.setMatrixUuidSet(matrixUuidSet);
        formAttributeVo.setMatrixUuidAttributeUuidSetMap(matrixUuidAttributeUuidSetMap);
    }
}

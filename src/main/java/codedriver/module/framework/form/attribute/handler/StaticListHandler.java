/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.form.attribute.core.IFormAttributeHandler;
import codedriver.framework.form.constvalue.FormConditionModel;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;

import java.util.List;

@Component
public class StaticListHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return "formstaticlist";
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        if (!attributeDataVo.dataIsEmpty()) {
            return "已更新";
        } else {
            return "";
        }
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resutlObj = new JSONObject();
        JSONArray dataObj = (JSONArray) attributeDataVo.getDataObj();
        if (CollectionUtils.isNotEmpty(dataObj)) {
            JSONArray attributeList = configObj.getJSONArray("attributeList");
            if (CollectionUtils.isNotEmpty(attributeList)) {
                JSONArray theadList = new JSONArray();
                for (int i = 0; i < attributeList.size(); i++) {
                    JSONObject attributeObj = attributeList.getJSONObject(i);
                    String attribute = attributeObj.getString("attribute");
                    JSONObject theadObj = new JSONObject();
                    theadObj.put("title", attribute);
                    theadObj.put("key", attribute);
                    theadList.add(theadObj);
                }
                JSONArray tbodyList = new JSONArray();
                for (int i = 0; i < dataObj.size(); i++) {
                    JSONObject tbodyObj = new JSONObject();
                    JSONArray rowData = dataObj.getJSONArray(i);
                    for (int j = 0; j < rowData.size(); j++) {
                        JSONObject cellObj = new JSONObject();
                        JSONObject attributeObj = attributeList.getJSONObject(j);
                        String attribute = attributeObj.getString("attribute");
                        String type = attributeObj.getString("type");
                        cellObj.put("type", type);
                        if ("text".equals(type)) {
                            String value = rowData.getString(j);
                            cellObj.put("value", value);
                            cellObj.put("text", value);
                        } else if ("textarea".equals(type)) {
                            String value = rowData.getString(j);
                            cellObj.put("value", value);
                            cellObj.put("text", value);
                        } else if ("select".equals(type)) {
                            String value = rowData.getString(j);
                            cellObj.put("value", value);
                            String text = "";
                            if (StringUtils.isNotBlank(value)) {
                                JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                                String dataSource = attrConfig.getString("dataSource");
                                if ("static".equals(dataSource)) {
                                    JSONArray dataList = attrConfig.getJSONArray("dataList");
                                    for (int k = 0; k < dataList.size(); k++) {
                                        JSONObject data = dataList.getJSONObject(k);
                                        String dataValue = data.getString("value");
                                        if (dataValue.equals(value)) {
                                            text = data.getString("text");
                                        }
                                    }
                                } else if ("matrix".equals(dataSource)) {
                                    String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
                                    if (split.length == 2) {
                                        text = split[1];
                                    } else {
                                        text = value;
                                    }
                                }
                            }
                            cellObj.put("text", text);
                        } else if ("selects".equals(type)) {
                            JSONArray valueList = rowData.getJSONArray(j);
                            cellObj.put("value", valueList);
                            JSONArray textList = new JSONArray();
                            if (CollectionUtils.isNotEmpty(valueList)) {
                                for (String value : valueList.toJavaList(String.class) ) {
                                    String text = "";
                                    if (StringUtils.isNotBlank(value)) {
                                        JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                                        String dataSource = attrConfig.getString("dataSource");
                                        if ("static".equals(dataSource)) {
                                            JSONArray dataList = attrConfig.getJSONArray("dataList");
                                            for (int k = 0; k < dataList.size(); k++) {
                                                JSONObject data = dataList.getJSONObject(k);
                                                String dataValue = data.getString("value");
                                                if (dataValue.equals(value)) {
                                                    text = data.getString("text");
                                                }
                                            }
                                        } else if ("matrix".equals(dataSource)) {
                                            String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
                                            if (split.length == 2) {
                                                text = split[1];
                                            } else {
                                                text = value;
                                            }
                                        }
                                    }
                                    textList.add(text);
                                }
                            }
                            cellObj.put("text", textList);
                        } else if ("radio".equals(type)) {
                            String value = rowData.getString(j);
                            cellObj.put("value", value);
                            String text = "";
                            if (StringUtils.isNotBlank(value)) {
                                JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                                String dataSource = attrConfig.getString("dataSource");
                                if ("static".equals(dataSource)) {
                                    JSONArray dataList = attrConfig.getJSONArray("dataList");
                                    for (int k = 0; k < dataList.size(); k++) {
                                        JSONObject data = dataList.getJSONObject(k);
                                        String dataValue = data.getString("value");
                                        if (dataValue.equals(value)) {
                                            text = data.getString("text");
                                        }
                                    }
                                } else if ("matrix".equals(dataSource)) {
                                    String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
                                    if (split.length == 2) {
                                        text = split[1];
                                    } else {
                                        text = value;
                                    }
                                }
                            }
                            cellObj.put("text", text);
                        } else if ("checkbox".equals(type)) {
                            JSONArray valueList = rowData.getJSONArray(j);
                            cellObj.put("value", valueList);
                            JSONArray textList = new JSONArray();
                            if (CollectionUtils.isNotEmpty(valueList)) {
                                for (String value : valueList.toJavaList(String.class) ) {
                                    String text = "";
                                    if (StringUtils.isNotBlank(value)) {
                                        JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                                        String dataSource = attrConfig.getString("dataSource");
                                        if ("static".equals(dataSource)) {
                                            JSONArray dataList = attrConfig.getJSONArray("dataList");
                                            for (int k = 0; k < dataList.size(); k++) {
                                                JSONObject data = dataList.getJSONObject(k);
                                                String dataValue = data.getString("value");
                                                if (dataValue.equals(value)) {
                                                    text = data.getString("text");
                                                }
                                            }
                                        } else if ("matrix".equals(dataSource)) {
                                            String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
                                            if (split.length == 2) {
                                                text = split[1];
                                            } else {
                                                text = value;
                                            }
                                        }
                                    }
                                    textList.add(text);
                                }
                            }
                            cellObj.put("text", textList);
                        } else if ("date".equals(type)) {
                            String value = rowData.getString(j);
                            cellObj.put("value", value);
                            cellObj.put("text", value);
                        } else if ("time".equals(type)) {
                            String value = rowData.getString(j);
                            cellObj.put("value", value);
                            cellObj.put("text", value);
                        } else if ("table".equals(type)) {
                            JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                            JSONArray valueList = rowData.getJSONArray(j);
                            AttributeDataVo attributeData = new AttributeDataVo();
                            attributeData.setDataObj(valueList);
                            Object tableData = dataTransformationForEmail(attributeData, attrConfig);
                            cellObj.put("value", valueList);
                            cellObj.put("text", tableData);
                        }
                        tbodyObj.put(attribute, cellObj);
                    }
                    tbodyList.add(tbodyObj);
                }
                resutlObj.put("theadList", theadList);
                resutlObj.put("tbodyList", tbodyList);
            }
        }
        return resutlObj;
    }

    @Override
    public Object textConversionValue(List<String> values, JSONObject config) {
        return null;
    }

    @Override
    public String getHandlerName() {
        return "表格输入组件";
    }

    @Override
    public String getIcon() {
        return "tsfont-formstaticlist";
    }

    @Override
    public ParamType getParamType() {
        return null;
    }

    @Override
    public String getDataType() {
        return null;
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
        return false;
    }

    @Override
    public boolean isFilterable() {
        return false;
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
    public String getHandlerType(FormConditionModel model) {
        return null;
    }
}

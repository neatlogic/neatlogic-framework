/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.form.attribute.core.FormHandlerBase;
import codedriver.framework.form.attribute.core.IFormAttributeHandler;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.constvalue.FormHandler;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.dto.FormAttributeVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.matrix.core.IMatrixDataSourceHandler;
import codedriver.framework.matrix.core.MatrixDataSourceHandlerFactory;
import codedriver.framework.matrix.dto.MatrixColumnVo;
import codedriver.framework.matrix.dto.MatrixDataVo;
import codedriver.framework.matrix.dto.MatrixVo;
import codedriver.framework.matrix.exception.MatrixDataSourceHandlerNotFoundException;
import codedriver.framework.matrix.exception.MatrixNotFoundException;
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
        return FormHandler.FORMCASCADELIST.getHandler();
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMCASCADELIST.getHandlerName();
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
    public boolean isAudit() {
        return true;
    }

    //表单组件配置信息
//{
//	"handler": "formcascadelist",
//	"label": "级联下拉_10",
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
//						"text": "二级1",
//						"value": "3"
//					},
//					{
//						"index": 2,
//						"text": "二级2",
//						"value": "4"
//					}
//				],
//				"index": 1,
//				"text": "一级1",
//				"value": "1"
//			},
//			{
//				"children": [
//					{
//						"index": 2,
//						"text": "二级3",
//						"value": "5"
//					},
//					{
//						"index": 2,
//						"text": "二级4",
//						"value": "6"
//					}
//				],
//				"index": 1,
//				"text": "一级2",
//				"value": "2"
//			}
//		],
//		"width": "100%",
//		"defaultValueType": "self",
//		"placeholder": [
//			"请选择一级下拉",
//			"请选择二级下拉",
//			"请选择三级下拉"
//		],
//		"authorityConfig": [
//			"common#alluser"
//		],
//		"dataSource": "static",
//		"direction": "transverse"
//	}
//}
    //保存数据结构
//    ["1","3"]
    //返回数据结构
//{
//	"textList": [
//		"一级1",
//		"二级1"
//	],
//	"valueList": [
//		"1",
//		"3"
//	]
//}
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
        formAttributeVo.setMatrixUuidSet(matrixUuidSet);
        formAttributeVo.setMatrixUuidAttributeUuidSetMap(matrixUuidAttributeUuidSetMap);
    }
}

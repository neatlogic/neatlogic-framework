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

@Component
public class RadioHandler extends FormHandlerBase {

    @Resource
    private FormService formService;

    @Override
    public String getHandler() {
        return FormHandler.FORMRADIO.getHandler();
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public int getSort() {
        return 5;
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        if (model == FormConditionModel.CUSTOM) {
            return "select";
        }
        return "radio";
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        String value = (String) attributeDataVo.getDataObj();
        if (StringUtils.isNotBlank(value)) {
            return getTextOrValue(value, configObj, ConversionType.TOTEXT.getValue());
        }
        return value;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return valueConversionText(attributeDataVo, configObj);
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return formService.textConversionValueForSelectHandler(text, config);
//        Object result = null;
//        if (CollectionUtils.isNotEmpty(values)) {
//            result = getTextOrValue(values.get(0), config, ConversionType.TOVALUE.getValue());
//        }
//        return result;
    }

    private Object getTextOrValue(String value, JSONObject configObj, String conversionType) {
        Object result = null;
        String dataSource = configObj.getString("dataSource");
        if ("static".equals(dataSource)) {
            List<ValueTextVo> dataList = JSON.parseArray(configObj.getString("dataList"), ValueTextVo.class);
            if (CollectionUtils.isNotEmpty(dataList)) {
                for (ValueTextVo data : dataList) {
                    if (ConversionType.TOTEXT.getValue().equals(conversionType) && value.equals(data.getValue())) {
                        result = data.getText();
                        break;
                    } else if (ConversionType.TOVALUE.getValue().equals(conversionType) && value.equals(data.getText())) {
                        result = data.getValue();
                        break;
                    }
                }
            }
        } else if ("matrix".equals(dataSource)) {// 其他，如动态数据源
            if (ConversionType.TOTEXT.getValue().equals(conversionType) && value.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
                result = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1];
            } else if (ConversionType.TOVALUE.getValue().equals(conversionType)) {
                String matrixUuid = configObj.getString("matrixUuid");
                ValueTextVo mapping = JSON.toJavaObject(configObj.getJSONObject("mapping"), ValueTextVo.class);
                if (StringUtils.isNotBlank(matrixUuid) && StringUtils.isNotBlank(value)
                        && mapping != null) {
                    result = getValue(matrixUuid, mapping, value);
                }
            }
        }
        return result;
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMRADIO.getHandlerName();
    }

    @Override
    public String getIcon() {
        return "tsfont-circle-o";
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
    public Boolean isNeedSliceWord() {
        return false;
    }

    //表单组件配置信息
//{
//	"handler": "formradio",
//	"label": "单选框_6",
//	"type": "form",
//	"uuid": "749af88f046d4478a2a3c31f4dd4ec3a",
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
//				"text": "单选1",
//				"value": "1"
//			},
//			{
//				"text": "单选2",
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
//    1
    //返回数据结构
//{
//	"textList": [
//		"单选1"
//	],
//	"valueList": [
//		"1"
//	]
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        String value = (String) attributeDataVo.getDataObj();
        if (StringUtils.isNotBlank(value)) {
            List<String> valueList = new ArrayList<>();
            valueList.add(value);
            List<String> textList = new ArrayList<>();
            String dataSource = configObj.getString("dataSource");
            if ("static".equals(dataSource)) {
                JSONArray dataArray = configObj.getJSONArray("dataList");
                if (CollectionUtils.isNotEmpty(dataArray)) {
                    List<ValueTextVo> dataList = dataArray.toJavaList(ValueTextVo.class);
                    for (ValueTextVo data : dataList) {
                        if (value.equals(data.getValue())) {
                            textList.add(data.getText());
                            break;
                        }
                    }
                }
            } else if ("matrix".equals(dataSource)) {// 其他，如动态数据源
                if (value.contains(IFormAttributeHandler.SELECT_COMPOSE_JOINER)) {
                    textList.add(value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER)[1]);
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

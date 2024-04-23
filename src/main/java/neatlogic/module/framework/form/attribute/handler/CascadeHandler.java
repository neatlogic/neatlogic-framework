/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.exception.AttributeValidException;
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
    public Object conversionDataType(Object source, String attributeLabel) {
        return convertToJSONArray(source, attributeLabel);
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
        "label": "级联下拉框_4",
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
//        else if ("matrix".equals(dataSource)) {// 其他，如动态数据源
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
        JSONObject config = formAttributeVo.getConfig();
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

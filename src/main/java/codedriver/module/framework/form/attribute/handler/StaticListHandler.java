/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.form.attribute.core.IFormAttributeHandler;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.dto.FormAttributeVo;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;

import java.util.*;

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
    //表单组件配置信息
//{
//	"handler": "formstaticlist",
//	"label": "表格输入组件_2",
//	"type": "form",
//	"uuid": "ca992985bd4b41db94688706807e64f7",
//	"config": {
//		"isRequired": false,
//		"relMatrixUuidList": [],
//		"ruleList": [],
//		"attributeList": [
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "文本框a",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": "文本框a",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "文本框1",
//				"type": "text"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "文本域b",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": "文本域b",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "文本域1",
//				"type": "textarea"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "1",
//					"dataList": [
//						{
//							"text": "下拉单选1",
//							"value": "1",
//							"isDefaultValue": true
//						},
//						{
//							"text": "下拉单选2",
//							"value": "2"
//						}
//					],
//					"value": "1",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "下拉单选1",
//				"type": "select"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": [
//						"1"
//					],
//					"dataList": [
//						{
//							"text": "下拉多选1",
//							"value": "1",
//							"isDefaultValue": true
//						},
//						{
//							"text": "下拉多选2",
//							"value": "2"
//						}
//					],
//					"value": [
//						"1"
//					],
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "下拉多选1",
//				"type": "selects"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "1",
//					"dataList": [
//						{
//							"text": "单选1",
//							"value": "1",
//							"isDefaultValue": true
//						},
//						{
//							"text": "单选2",
//							"value": "2"
//						}
//					],
//					"value": "1",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "单选框1",
//				"type": "radio"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": [
//						"1"
//					],
//					"dataList": [
//						{
//							"text": "复选1",
//							"value": "1",
//							"isDefaultValue": true
//						},
//						{
//							"text": "复选2",
//							"value": "2"
//						}
//					],
//					"value": [
//						"1"
//					],
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "复选框1",
//				"type": "checkbox"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "2022-02-21",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": "2022-02-21",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "日期1",
//				"type": "date"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "00:00:05",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": "00:00:05",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "时间1",
//				"type": "time"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attrConfig": {
//					"attributeList": [
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "文本框b",
//								"dataList": [
//									{
//										"text": "",
//										"value": ""
//									}
//								],
//								"value": "文本框b",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "文本框2",
//							"type": "text"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "文本域c",
//								"dataList": [
//									{
//										"text": "",
//										"value": ""
//									}
//								],
//								"value": "文本域c",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "文本域2",
//							"type": "textarea"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "1",
//								"dataList": [
//									{
//										"text": "下拉单选1",
//										"value": "1",
//										"isDefaultValue": true
//									},
//									{
//										"text": "下拉单选2",
//										"value": "2"
//									}
//								],
//								"value": "1",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "下拉单选2",
//							"type": "select"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": [
//									"1"
//								],
//								"dataList": [
//									{
//										"text": "下拉多选1",
//										"value": "1",
//										"isDefaultValue": true
//									},
//									{
//										"text": "下拉多选2",
//										"value": "2"
//									}
//								],
//								"value": [
//									"1"
//								],
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "下拉多选2",
//							"type": "selects"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "1",
//								"dataList": [
//									{
//										"text": "单选1",
//										"value": "1",
//										"isDefaultValue": true
//									},
//									{
//										"text": "单选2",
//										"value": "2"
//									}
//								],
//								"value": "1",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "单选框2",
//							"type": "radio"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": [
//									"1"
//								],
//								"dataList": [
//									{
//										"text": "复选1",
//										"value": "1",
//										"isDefaultValue": true
//									},
//									{
//										"text": "复选2",
//										"value": "2"
//									}
//								],
//								"value": [
//									"1"
//								],
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "复选框2",
//							"type": "checkbox"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "2022-02-22",
//								"dataList": [
//									{
//										"text": "",
//										"value": ""
//									}
//								],
//								"value": "2022-02-22",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "日期1",
//							"type": "date"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "05:05:05",
//								"dataList": [
//									{
//										"text": "",
//										"value": ""
//									}
//								],
//								"value": "05:05:05",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "时间2",
//							"type": "time"
//						}
//					],
//					"value": ""
//				},
//				"attribute": "表格1",
//				"type": "table"
//			}
//		],
//		"width": "100%",
//		"validList": [],
//		"quoteUuid": "",
//		"defaultValueType": "self",
//		"authorityConfig": [
//			"common#alluser"
//		]
//	}
//}
    //保存数据结构
//[
//	[
//		"文本框a",
//		"文本域b",
//		"1",
//		"111&=&2022-02-22 00:00:00",
//		[
//			"1"
//		],
//		[
//			"111&=&闫雅"
//		],
//		"1",
//		[
//			"1"
//		],
//		"2022-02-21",
//		"00:00:05",
//		[
//			[
//				"文本框b",
//				"文本域c",
//				"1",
//				[
//					"1"
//				],
//				"1",
//				[
//					"1"
//				],
//				"2022-02-22",
//				"05:05:05"
//			]
//		]
//	],
//	[
//		"文本框a2",
//		"文本域b2",
//		"1",
//		"222&=&2022-02-21 00:00:00",
//		[
//			"1"
//		],
//		[
//			"222&=&蒋琪"
//		],
//		"1",
//		[
//			"1"
//		],
//		"2022-02-21",
//		"00:00:05",
//		[
//			[
//				"文本框b",
//				"文本域c",
//				"1",
//				[
//					"1"
//				],
//				"1",
//				[
//					"1"
//				],
//				"2022-02-22",
//				"05:05:05"
//			]
//		]
//	]
//]
    //返回数据结构
// {
//  "value": 原始数据
//	"theadList": [
//		{
//			"title": "文本框1",
//			"key": "文本框1"
//		},
//		{
//			"title": "文本域1",
//			"key": "文本域1"
//		},
//		{
//			"title": "下拉单选1",
//			"key": "下拉单选1"
//		},
//		{
//			"title": "下拉单选2",
//			"key": "下拉单选2"
//		},
//		{
//			"title": "下拉多选1",
//			"key": "下拉多选1"
//		},
//		{
//			"title": "下拉多选2",
//			"key": "下拉多选2"
//		},
//		{
//			"title": "单选框1",
//			"key": "单选框1"
//		},
//		{
//			"title": "复选框1",
//			"key": "复选框1"
//		},
//		{
//			"title": "日期1",
//			"key": "日期1"
//		},
//		{
//			"title": "时间1",
//			"key": "时间1"
//		},
//		{
//			"title": "表格1",
//			"key": "表格1"
//		}
//	],
//	"tbodyList": [
//		{
//			"文本域1": {
//				"text": "文本域b",
//				"type": "textarea",
//				"value": "文本域b"
//			},
//			"复选框1": {
//				"text": [
//					"复选1"
//				],
//				"type": "checkbox",
//				"value": [
//					"1"
//				]
//			},
//			"时间1": {
//				"text": "00:00:05",
//				"type": "time",
//				"value": "00:00:05"
//			},
//			"单选框1": {
//				"text": "单选1",
//				"type": "radio",
//				"value": "1"
//			},
//			"文本框1": {
//				"text": "文本框a",
//				"type": "text",
//				"value": "文本框a"
//			},
//			"下拉单选2": {
//				"text": "2022-02-22 00:00:00",
//				"type": "select",
//				"value": "111&=&2022-02-22 00:00:00"
//			},
//			"下拉单选1": {
//				"text": "下拉单选1",
//				"type": "select",
//				"value": "1"
//			},
//			"日期1": {
//				"text": "2022-02-21",
//				"type": "date",
//				"value": "2022-02-21"
//			},
//			"下拉多选2": {
//				"text": [
//					"闫雅"
//				],
//				"type": "selects",
//				"value": [
//					"111&=&闫雅"
//				]
//			},
//			"下拉多选1": {
//				"text": [
//					"下拉多选1"
//				],
//				"type": "selects",
//				"value": [
//					"1"
//				]
//			},
//			"表格1": {
//				"text": {
//					"theadList": [
//						{
//							"title": "文本框2",
//							"key": "文本框2"
//						},
//						{
//							"title": "文本域2",
//							"key": "文本域2"
//						},
//						{
//							"title": "下拉单选2",
//							"key": "下拉单选2"
//						},
//						{
//							"title": "下拉多选2",
//							"key": "下拉多选2"
//						},
//						{
//							"title": "单选框2",
//							"key": "单选框2"
//						},
//						{
//							"title": "复选框2",
//							"key": "复选框2"
//						},
//						{
//							"title": "日期1",
//							"key": "日期1"
//						},
//						{
//							"title": "时间2",
//							"key": "时间2"
//						}
//					],
//					"tbodyList": [
//						{
//							"时间2": {
//								"text": "05:05:05",
//								"type": "time",
//								"value": "05:05:05"
//							},
//							"下拉单选2": {
//								"text": "下拉单选1",
//								"type": "select",
//								"value": "1"
//							},
//							"文本框2": {
//								"text": "文本框b",
//								"type": "text",
//								"value": "文本框b"
//							},
//							"单选框2": {
//								"text": "单选1",
//								"type": "radio",
//								"value": "1"
//							},
//							"文本域2": {
//								"text": "文本域c",
//								"type": "textarea",
//								"value": "文本域c"
//							},
//							"复选框2": {
//								"text": [
//									"复选1"
//								],
//								"type": "checkbox",
//								"value": [
//									"1"
//								]
//							},
//							"日期1": {
//								"text": "2022-02-22",
//								"type": "date",
//								"value": "2022-02-22"
//							},
//							"下拉多选2": {
//								"text": [
//									"下拉多选1"
//								],
//								"type": "selects",
//								"value": [
//									"1"
//								]
//							}
//						}
//					]
//				},
//				"type": "table",
//				"value": [
//					[
//						"文本框b",
//						"文本域c",
//						"1",
//						[
//							"1"
//						],
//						"1",
//						[
//							"1"
//						],
//						"2022-02-22",
//						"05:05:05"
//					]
//				]
//			}
//		}
//	]
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        JSONArray dataObj = (JSONArray) attributeDataVo.getDataObj();
        resultObj.put("value", dataObj);
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
                            Object tableData = getMyDetailedData(attributeData, attrConfig);
                            cellObj.put("value", valueList);
                            cellObj.put("text", tableData);
                        }
                        tbodyObj.put(attribute, cellObj);
                    }
                    tbodyList.add(tbodyObj);
                }
                resultObj.put("theadList", theadList);
                resultObj.put("tbodyList", tbodyList);
            }
        }
        return resultObj;
    }

    @Override
    public void makeupFormAttribute(FormAttributeVo formAttributeVo) {
        Set<String> matrixUuidSet = new HashSet<>();
        Map<String, Set<String>> matrixUuidAttributeUuidSetMap = new HashMap<>();
        JSONObject config = formAttributeVo.getConfigObj();
        /** 扩展属性 **/
        JSONArray attributeArray = config.getJSONArray("attributeList");
        if (CollectionUtils.isNotEmpty(attributeArray)) {
            for (int i = 0; i < attributeArray.size(); i++) {
                JSONObject attributeObj = attributeArray.getJSONObject(i);
                if (MapUtils.isNotEmpty(attributeObj)) {
                    JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                    if (MapUtils.isNotEmpty(attrConfig)) {
                        String dataSource = attrConfig.getString("dataSource");
                        if ("matrix".equals(dataSource)) {
                            parseExtendAttribute(attrConfig, matrixUuidSet, matrixUuidAttributeUuidSetMap);
                        } else {
                            String type = attrConfig.getString("type");
                            if ("table".equals(type)) {
                                attributeArray = attrConfig.getJSONArray("attributeList");
                                if (CollectionUtils.isNotEmpty(attributeArray)) {
                                    for (int j = 0; j < attributeArray.size(); j++) {
                                        attributeObj = attributeArray.getJSONObject(i);
                                        if (MapUtils.isNotEmpty(attributeObj)) {
                                            attrConfig = attributeObj.getJSONObject("attrConfig");
                                            if (MapUtils.isNotEmpty(attrConfig)) {
                                                dataSource = attrConfig.getString("dataSource");
                                                if ("matrix".equals(dataSource)) {
                                                    parseExtendAttribute(attrConfig, matrixUuidSet, matrixUuidAttributeUuidSetMap);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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

    private void parseExtendAttribute(JSONObject attrConfig, Set<String> matrixUuidSet, Map<String, Set<String>> matrixUuidAttributeUuidSetMap) {
        String matrixUuid = attrConfig.getString("matrixUuid");
        if (StringUtils.isNotBlank(matrixUuid)) {
            Set<String> attributeUuidSet = new HashSet<>();
            matrixUuidSet.add(matrixUuid);
            JSONObject mapping = attrConfig.getJSONObject("mapping");
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
            Set<String> oldAttributeUuidSet = matrixUuidAttributeUuidSetMap.get(matrixUuid);
            if (oldAttributeUuidSet != null) {
                oldAttributeUuidSet.addAll(attributeUuidSet);
            } else {
                matrixUuidAttributeUuidSetMap.put(matrixUuid, attributeUuidSet);
            }
        }
    }
}

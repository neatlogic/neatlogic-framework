/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.attribute.core.FormHandlerBase;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.constvalue.FormHandler;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.dto.FormAttributeVo;
import codedriver.framework.form.exception.AttributeValidException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StaticListHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMSTATICLIST.getHandler();
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
        return getMyDetailedData(attributeDataVo, configObj);
    }

    @Override
    public Object textConversionValue(List<String> values, JSONObject config) {
        return null;
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMSTATICLIST.getHandlerName();
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
//	"label": "表格输入组件_1",
//	"type": "form",
//	"uuid": "98daacc1b4004aada6a78d2e01e48620",
//	"config": {
//		"isRequired": false,
//		"relMatrixUuidList": [
//			"8db9a7aa7f8e485d86c97d6ba6d37ae0",
//			"8db9a7aa7f8e485d86c97d6ba6d37ae0"
//		],
//		"ruleList": [],
//		"attributeList": [
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "74664b7840354e6eb9c48bac2548e1ad",
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "文本框1",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": "文本框1",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "文本框1",
//				"type": "text"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "1e0ed7ac19bc4f1fa013f2a74df4e816",
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "文本域1",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": "文本域1",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "文本域1",
//				"type": "textarea"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "9c6cc7e4c3734a77a5e4953d48f3d906",
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "",
//					"dataList": [
//						{
//							"text": "下拉单选1",
//							"value": "1"
//						},
//						{
//							"text": "下拉单选2",
//							"value": "2"
//						}
//					],
//					"value": "",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "下拉单选1",
//				"type": "select"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "b02b4242d24a4aacaea9eda7cb744388",
//				"attrConfig": {
//					"mapping": {
//						"text": "bc0afbec74ea4936acf62ba30e332854",
//						"value": "e31052d7732345fd825c43a9f9deacb1"
//					},
//					"defaultValue": "",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": "",
//					"dataSource": "matrix",
//					"matrixUuid": "8db9a7aa7f8e485d86c97d6ba6d37ae0"
//				},
//				"attribute": "下拉单选3",
//				"type": "select"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "75d1991fa9c14705b7ff9534e296f7c3",
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": [],
//					"dataList": [
//						{
//							"text": "下拉多选1",
//							"value": "1"
//						},
//						{
//							"text": "下拉多选2",
//							"value": "2"
//						}
//					],
//					"value": [],
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "下拉多选1",
//				"type": "selects"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "1b11c24afbc344d18408f177662b9094",
//				"attrConfig": {
//					"mapping": {
//						"text": "bc0afbec74ea4936acf62ba30e332854",
//						"value": "e31052d7732345fd825c43a9f9deacb1"
//					},
//					"defaultValue": "",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": [],
//					"dataSource": "matrix",
//					"matrixUuid": "8db9a7aa7f8e485d86c97d6ba6d37ae0"
//				},
//				"attribute": "下拉多选3",
//				"type": "selects"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "3f2dd667710f4d0ebeb0c633c46cc0b2",
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "",
//					"dataList": [
//						{
//							"text": "单选1",
//							"value": "1"
//						},
//						{
//							"text": "单选2",
//							"value": "2"
//						}
//					],
//					"value": "",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "单选框1",
//				"type": "radio"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "9ad9bd250ef24c52b565aeaf4cbe8868",
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": [],
//					"dataList": [
//						{
//							"text": "复选1",
//							"value": "1"
//						},
//						{
//							"text": "复选2",
//							"value": "2"
//						}
//					],
//					"value": [],
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "复选框1",
//				"type": "checkbox"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "9e682c9464df4ffabbff5ed1c668a573",
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "2022-03-08",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": "2022-03-08",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "日期1",
//				"type": "date"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "0c25cda11ee349f8bf5d7b69d7939b08",
//				"attrConfig": {
//					"mapping": {
//						"text": "",
//						"value": ""
//					},
//					"defaultValue": "05:05:05",
//					"dataList": [
//						{
//							"text": "",
//							"value": ""
//						}
//					],
//					"value": "05:05:05",
//					"dataSource": "static",
//					"matrixUuid": ""
//				},
//				"attribute": "时间1",
//				"type": "time"
//			},
//			{
//				"isRequired": false,
//				"valid_o": false,
//				"attributeUuid": "31d847ff3e7e4faf948051480ba6cc83",
//				"attrConfig": {
//					"attributeList": [
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attributeUuid": "3203b5159766483aa125f41db2e6ce11",
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "文本框2",
//								"dataList": [
//									{
//										"text": "",
//										"value": ""
//									}
//								],
//								"value": "文本框2",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "文本框2",
//							"type": "text"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attributeUuid": "30863008c02d4cc1ad04536a02b507a6",
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "文本域2",
//								"dataList": [
//									{
//										"text": "",
//										"value": ""
//									}
//								],
//								"value": "文本域2",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "文本域2",
//							"type": "textarea"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attributeUuid": "791cbe948bd244059a7458cd57460eee",
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "",
//								"dataList": [
//									{
//										"text": "下拉单选1",
//										"value": "1"
//									},
//									{
//										"text": "下拉多选2",
//										"value": "2"
//									}
//								],
//								"value": "",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "下拉单选2",
//							"type": "select"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attributeUuid": "b401ec9066084c89983dc91f73de0251",
//							"attrConfig": {
//								"mapping": {
//									"text": "bc0afbec74ea4936acf62ba30e332854",
//									"value": "e31052d7732345fd825c43a9f9deacb1"
//								},
//								"defaultValue": "",
//								"dataList": [
//									{
//										"text": "",
//										"value": ""
//									}
//								],
//								"value": "",
//								"dataSource": "matrix",
//								"matrixUuid": "8db9a7aa7f8e485d86c97d6ba6d37ae0"
//							},
//							"attribute": "下拉单选4",
//							"type": "select"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attributeUuid": "f74d3b38d17343318aa01c7d8d764f05",
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": [],
//								"dataList": [
//									{
//										"text": "下拉多选1",
//										"value": "1"
//									},
//									{
//										"text": "下拉多选2",
//										"value": "2"
//									}
//								],
//								"value": [],
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "下拉多选2",
//							"type": "selects"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attributeUuid": "ca9efbd9e6d1463f91e31bd9c016de57",
//							"attrConfig": {
//								"mapping": {
//									"text": "bc0afbec74ea4936acf62ba30e332854",
//									"value": "e31052d7732345fd825c43a9f9deacb1"
//								},
//								"defaultValue": "",
//								"dataList": [
//									{
//										"text": "",
//										"value": ""
//									}
//								],
//								"value": [],
//								"dataSource": "matrix",
//								"matrixUuid": "8db9a7aa7f8e485d86c97d6ba6d37ae0"
//							},
//							"attribute": "下拉多选4",
//							"type": "selects"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attributeUuid": "d5f315239355483cb8093358d4bd0f58",
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "",
//								"dataList": [
//									{
//										"text": "单选1",
//										"value": "1"
//									},
//									{
//										"text": "单选2",
//										"value": "2"
//									}
//								],
//								"value": "",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "单选框2",
//							"type": "radio"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attributeUuid": "b8a5317c277a4327b3eb0cb26ba5808d",
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": [],
//								"dataList": [
//									{
//										"text": "复选1",
//										"value": "1"
//									},
//									{
//										"text": "复选2",
//										"value": "2"
//									}
//								],
//								"value": [],
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "复选框2",
//							"type": "checkbox"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attributeUuid": "1b590cd6cc494e75ad175086492db1a6",
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "2022-03-09",
//								"dataList": [
//									{
//										"text": "",
//										"value": ""
//									}
//								],
//								"value": "2022-03-09",
//								"dataSource": "static",
//								"matrixUuid": ""
//							},
//							"attribute": "日期2",
//							"type": "date"
//						},
//						{
//							"isRequired": false,
//							"valid_o": false,
//							"attributeUuid": "4748dae675684fdc8abca24f39a4e91c",
//							"attrConfig": {
//								"mapping": {
//									"text": "",
//									"value": ""
//								},
//								"defaultValue": "10:05:08",
//								"dataList": [
//									{
//										"text": "",
//										"value": ""
//									}
//								],
//								"value": "10:05:08",
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
//{
//	"selectUuidList": [
//		"eeb69086216c44659be3cfca15328c69"
//	],
//	"detailData": {
//		"eeb69086216c44659be3cfca15328c69": {
//			"3f2dd667710f4d0ebeb0c633c46cc0b2": {
//				"text": "单选1",
//				"type": "radio",
//				"value": "1"
//			},
//			"b02b4242d24a4aacaea9eda7cb744388": {
//				"text": "2022-03-08 00:00:00",
//				"type": "select",
//				"value": "222&=&2022-03-08 00:00:00"
//			},
//			"74664b7840354e6eb9c48bac2548e1ad": {
//				"text": "text1",
//				"type": "text",
//				"value": "text1"
//			},
//			"1e0ed7ac19bc4f1fa013f2a74df4e816": {
//				"text": "文本域1",
//				"type": "textarea",
//				"value": "文本域1"
//			},
//			"31d847ff3e7e4faf948051480ba6cc83": {
//				"type": "table",
//				"value": {
//					"8bf5aa9404434eb493812a66c59f63d0": {
//						"30863008c02d4cc1ad04536a02b507a6": {
//							"text": "文本域2",
//							"type": "textarea",
//							"value": "文本域2"
//						},
//						"d5f315239355483cb8093358d4bd0f58": {
//							"text": "单选1",
//							"type": "radio",
//							"value": "1"
//						},
//						"b401ec9066084c89983dc91f73de0251": {
//							"text": "2022-03-09 00:00:00",
//							"type": "select",
//							"value": "111&=&2022-03-09 00:00:00"
//						},
//						"4748dae675684fdc8abca24f39a4e91c": {
//							"text": "10:05:08",
//							"type": "time",
//							"value": "10:05:08"
//						},
//						"1b590cd6cc494e75ad175086492db1a6": {
//							"text": "2022-03-09",
//							"type": "date",
//							"value": "2022-03-09"
//						},
//						"3203b5159766483aa125f41db2e6ce11": {
//							"text": "文本框2",
//							"type": "text",
//							"value": "文本框2"
//						},
//						"b8a5317c277a4327b3eb0cb26ba5808d": {
//							"text": [
//								"复选1"
//							],
//							"type": "checkbox",
//							"value": [
//								"1"
//							]
//						},
//						"ca9efbd9e6d1463f91e31bd9c016de57": {
//							"text": [
//								"2022-03-09 00:00:00"
//							],
//							"type": "selects",
//							"value": [
//								"111&=&2022-03-09 00:00:00"
//							]
//						},
//						"791cbe948bd244059a7458cd57460eee": {
//							"text": "下拉单选1",
//							"type": "select",
//							"value": "1"
//						},
//						"f74d3b38d17343318aa01c7d8d764f05": {
//							"text": [
//								"下拉多选2"
//							],
//							"type": "selects",
//							"value": [
//								"2"
//							]
//						}
//					}
//				}
//			},
//			"75d1991fa9c14705b7ff9534e296f7c3": {
//				"text": [
//					"下拉多选1"
//				],
//				"type": "selects",
//				"value": [
//					"1"
//				]
//			},
//			"9e682c9464df4ffabbff5ed1c668a573": {
//				"text": "2022-03-08",
//				"type": "date",
//				"value": "2022-03-08"
//			},
//			"0c25cda11ee349f8bf5d7b69d7939b08": {
//				"text": "05:05:05",
//				"type": "time",
//				"value": "05:05:05"
//			},
//			"9ad9bd250ef24c52b565aeaf4cbe8868": {
//				"text": [
//					"复选1"
//				],
//				"type": "checkbox",
//				"value": [
//					"1"
//				]
//			},
//			"1b11c24afbc344d18408f177662b9094": {
//				"text": [
//					"2022-03-09 00:00:00"
//				],
//				"type": "selects",
//				"value": [
//					"111&=&2022-03-09 00:00:00"
//				]
//			},
//			"9c6cc7e4c3734a77a5e4953d48f3d906": {
//				"text": "下拉单选1",
//				"type": "select",
//				"value": "1"
//			}
//		}
//	},
//	"extendedData": {
//		"eeb69086216c44659be3cfca15328c69": {
//			"3f2dd667710f4d0ebeb0c633c46cc0b2": "1",
//			"b02b4242d24a4aacaea9eda7cb744388": "222&=&2022-03-08 00:00:00",
//			"74664b7840354e6eb9c48bac2548e1ad": "text1",
//			"1e0ed7ac19bc4f1fa013f2a74df4e816": "文本域1",
//			"31d847ff3e7e4faf948051480ba6cc83": {
//				"8bf5aa9404434eb493812a66c59f63d0": {
//					"30863008c02d4cc1ad04536a02b507a6": "文本域2",
//					"d5f315239355483cb8093358d4bd0f58": "1",
//					"b401ec9066084c89983dc91f73de0251": "111&=&2022-03-09 00:00:00",
//					"4748dae675684fdc8abca24f39a4e91c": "10:05:08",
//					"1b590cd6cc494e75ad175086492db1a6": "2022-03-09",
//					"3203b5159766483aa125f41db2e6ce11": "文本框2",
//					"b8a5317c277a4327b3eb0cb26ba5808d": [
//						"1"
//					],
//					"ca9efbd9e6d1463f91e31bd9c016de57": [
//						"111&=&2022-03-09 00:00:00"
//					],
//					"791cbe948bd244059a7458cd57460eee": "1",
//					"f74d3b38d17343318aa01c7d8d764f05": [
//						"2"
//					]
//				}
//			},
//			"75d1991fa9c14705b7ff9534e296f7c3": [
//				"1"
//			],
//			"9e682c9464df4ffabbff5ed1c668a573": "2022-03-08",
//			"0c25cda11ee349f8bf5d7b69d7939b08": "05:05:05",
//			"9ad9bd250ef24c52b565aeaf4cbe8868": [
//				"1"
//			],
//			"1b11c24afbc344d18408f177662b9094": [
//				"111&=&2022-03-09 00:00:00"
//			],
//			"9c6cc7e4c3734a77a5e4953d48f3d906": "1"
//		}
//	}
//}
    //返回数据结构
//{
//	"value": 原始数据,
//	"selectUuidList": [
//		"eeb69086216c44659be3cfca15328c69",
//		"7033e2ba4d7845ee815ede897ce3b399"
//	],
//	"theadList": [
//		{
//			"title": "文本框1",
//			"key": "74664b7840354e6eb9c48bac2548e1ad"
//		},
//		{
//			"title": "文本域1",
//			"key": "1e0ed7ac19bc4f1fa013f2a74df4e816"
//		},
//		{
//			"title": "下拉单选1",
//			"key": "9c6cc7e4c3734a77a5e4953d48f3d906"
//		},
//		{
//			"title": "下拉单选3",
//			"key": "b02b4242d24a4aacaea9eda7cb744388"
//		},
//		{
//			"title": "下拉多选1",
//			"key": "75d1991fa9c14705b7ff9534e296f7c3"
//		},
//		{
//			"title": "下拉多选3",
//			"key": "1b11c24afbc344d18408f177662b9094"
//		},
//		{
//			"title": "单选框1",
//			"key": "3f2dd667710f4d0ebeb0c633c46cc0b2"
//		},
//		{
//			"title": "复选框1",
//			"key": "9ad9bd250ef24c52b565aeaf4cbe8868"
//		},
//		{
//			"title": "日期1",
//			"key": "9e682c9464df4ffabbff5ed1c668a573"
//		},
//		{
//			"title": "时间1",
//			"key": "0c25cda11ee349f8bf5d7b69d7939b08"
//		},
//		{
//			"title": "表格1",
//			"key": "31d847ff3e7e4faf948051480ba6cc83"
//		}
//	],
//	"tbodyList": [
//		{
//			"3f2dd667710f4d0ebeb0c633c46cc0b2": {
//				"text": "单选1",
//				"type": "radio",
//				"value": "1"
//			},
//			"b02b4242d24a4aacaea9eda7cb744388": {
//				"text": "2022-03-08 00:00:00",
//				"type": "select",
//				"value": "222&=&2022-03-08 00:00:00"
//			},
//			"74664b7840354e6eb9c48bac2548e1ad": {
//				"text": "text1",
//				"type": "text",
//				"value": "text1"
//			},
//			"1e0ed7ac19bc4f1fa013f2a74df4e816": {
//				"text": "文本域1",
//				"type": "textarea",
//				"value": "文本域1"
//			},
//			"31d847ff3e7e4faf948051480ba6cc83": {
//				"text": {
//					"theadList": [
//						{
//							"title": "文本框2",
//							"key": "3203b5159766483aa125f41db2e6ce11"
//						},
//						{
//							"title": "文本域2",
//							"key": "30863008c02d4cc1ad04536a02b507a6"
//						},
//						{
//							"title": "下拉单选2",
//							"key": "791cbe948bd244059a7458cd57460eee"
//						},
//						{
//							"title": "下拉单选4",
//							"key": "b401ec9066084c89983dc91f73de0251"
//						},
//						{
//							"title": "下拉多选2",
//							"key": "f74d3b38d17343318aa01c7d8d764f05"
//						},
//						{
//							"title": "下拉多选4",
//							"key": "ca9efbd9e6d1463f91e31bd9c016de57"
//						},
//						{
//							"title": "单选框2",
//							"key": "d5f315239355483cb8093358d4bd0f58"
//						},
//						{
//							"title": "复选框2",
//							"key": "b8a5317c277a4327b3eb0cb26ba5808d"
//						},
//						{
//							"title": "日期2",
//							"key": "1b590cd6cc494e75ad175086492db1a6"
//						},
//						{
//							"title": "时间2",
//							"key": "4748dae675684fdc8abca24f39a4e91c"
//						}
//					],
//					"tbodyList": [
//						{
//							"30863008c02d4cc1ad04536a02b507a6": {
//								"text": "文本域2",
//								"type": "textarea",
//								"value": "文本域2"
//							},
//							"d5f315239355483cb8093358d4bd0f58": {
//								"text": "单选1",
//								"type": "radio",
//								"value": "1"
//							},
//							"b401ec9066084c89983dc91f73de0251": {
//								"text": "2022-03-09 00:00:00",
//								"type": "select",
//								"value": "111&=&2022-03-09 00:00:00"
//							},
//							"4748dae675684fdc8abca24f39a4e91c": {
//								"text": "10:05:08",
//								"type": "time",
//								"value": "10:05:08"
//							},
//							"1b590cd6cc494e75ad175086492db1a6": {
//								"text": "2022-03-09",
//								"type": "date",
//								"value": "2022-03-09"
//							},
//							"3203b5159766483aa125f41db2e6ce11": {
//								"text": "文本框2",
//								"type": "text",
//								"value": "文本框2"
//							},
//							"b8a5317c277a4327b3eb0cb26ba5808d": {
//								"text": [
//									"复选1"
//								],
//								"type": "checkbox",
//								"value": [
//									"1"
//								]
//							},
//							"ca9efbd9e6d1463f91e31bd9c016de57": {
//								"text": [
//									"2022-03-09 00:00:00"
//								],
//								"type": "selects",
//								"value": [
//									"111&=&2022-03-09 00:00:00"
//								]
//							},
//							"791cbe948bd244059a7458cd57460eee": {
//								"text": "下拉单选1",
//								"type": "select",
//								"value": "1"
//							},
//							"f74d3b38d17343318aa01c7d8d764f05": {
//								"text": [
//									"下拉多选2"
//								],
//								"type": "selects",
//								"value": [
//									"2"
//								]
//							}
//						}
//					]
//				},
//				"type": "table",
//				"value": {
//					"8bf5aa9404434eb493812a66c59f63d0": {
//						"30863008c02d4cc1ad04536a02b507a6": {
//							"text": "文本域2",
//							"type": "textarea",
//							"value": "文本域2"
//						},
//						"d5f315239355483cb8093358d4bd0f58": {
//							"text": "单选1",
//							"type": "radio",
//							"value": "1"
//						},
//						"b401ec9066084c89983dc91f73de0251": {
//							"text": "2022-03-09 00:00:00",
//							"type": "select",
//							"value": "111&=&2022-03-09 00:00:00"
//						},
//						"4748dae675684fdc8abca24f39a4e91c": {
//							"text": "10:05:08",
//							"type": "time",
//							"value": "10:05:08"
//						},
//						"1b590cd6cc494e75ad175086492db1a6": {
//							"text": "2022-03-09",
//							"type": "date",
//							"value": "2022-03-09"
//						},
//						"3203b5159766483aa125f41db2e6ce11": {
//							"text": "文本框2",
//							"type": "text",
//							"value": "文本框2"
//						},
//						"b8a5317c277a4327b3eb0cb26ba5808d": {
//							"text": [
//								"复选1"
//							],
//							"type": "checkbox",
//							"value": [
//								"1"
//							]
//						},
//						"ca9efbd9e6d1463f91e31bd9c016de57": {
//							"text": [
//								"2022-03-09 00:00:00"
//							],
//							"type": "selects",
//							"value": [
//								"111&=&2022-03-09 00:00:00"
//							]
//						},
//						"791cbe948bd244059a7458cd57460eee": {
//							"text": "下拉单选1",
//							"type": "select",
//							"value": "1"
//						},
//						"f74d3b38d17343318aa01c7d8d764f05": {
//							"text": [
//								"下拉多选2"
//							],
//							"type": "selects",
//							"value": [
//								"2"
//							]
//						}
//					}
//				}
//			},
//			"75d1991fa9c14705b7ff9534e296f7c3": {
//				"text": [
//					"下拉多选1"
//				],
//				"type": "selects",
//				"value": [
//					"1"
//				]
//			},
//			"9e682c9464df4ffabbff5ed1c668a573": {
//				"text": "2022-03-08",
//				"type": "date",
//				"value": "2022-03-08"
//			},
//			"0c25cda11ee349f8bf5d7b69d7939b08": {
//				"text": "05:05:05",
//				"type": "time",
//				"value": "05:05:05"
//			},
//			"9ad9bd250ef24c52b565aeaf4cbe8868": {
//				"text": [
//					"复选1"
//				],
//				"type": "checkbox",
//				"value": [
//					"1"
//				]
//			},
//			"1b11c24afbc344d18408f177662b9094": {
//				"text": [
//					"2022-03-09 00:00:00"
//				],
//				"type": "selects",
//				"value": [
//					"111&=&2022-03-09 00:00:00"
//				]
//			},
//			"9c6cc7e4c3734a77a5e4953d48f3d906": {
//				"text": "下拉单选1",
//				"type": "select",
//				"value": "1"
//			}
//		}
//	]
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        JSONObject dataObj = (JSONObject) attributeDataVo.getDataObj();
        resultObj.put("value", dataObj);
        if (MapUtils.isEmpty(dataObj)) {
            return resultObj;
        }
        JSONArray selectUuidArray = dataObj.getJSONArray("selectUuidList");
        if (CollectionUtils.isEmpty(selectUuidArray)) {
            return resultObj;
        }
        resultObj.put("selectUuidList", selectUuidArray);
        JSONObject detailData = dataObj.getJSONObject("detailData");
        if (MapUtils.isEmpty(detailData)) {
            return resultObj;
        }
        List<String> selectUuidList = selectUuidArray.toJavaList(String.class);
        JSONObject tableObj = convertTableData(detailData, configObj, selectUuidList);
        resultObj.putAll(tableObj);
        return resultObj;
    }

    /**
     * 将原始数据转换成表格数据（包含theadList、tbodyList）
     *
     * @param dataObj        原始数据
     * @param configObj      配置信息
     * @param selectUuidList 选中的行列表
     * @return
     */
    private JSONObject convertTableData(JSONObject dataObj, JSONObject configObj, List<String> selectUuidList) {
        JSONObject resultObj = new JSONObject();
        JSONArray attributeList = configObj.getJSONArray("attributeList");
        if (CollectionUtils.isEmpty(attributeList)) {
            return resultObj;
        }
        JSONArray theadList = new JSONArray();
        for (int i = 0; i < attributeList.size(); i++) {
            JSONObject attributeObj = attributeList.getJSONObject(i);
            String attributeUuid = attributeObj.getString("attributeUuid");
            String attribute = attributeObj.getString("attribute");
            JSONObject theadObj = new JSONObject();
            theadObj.put("title", attribute);
            theadObj.put("key", attributeUuid);
            theadList.add(theadObj);
        }
        JSONArray tbodyList = new JSONArray();
        for (Map.Entry<String, Object> rowDataObj : dataObj.entrySet()) {
            String rowUuid = rowDataObj.getKey();
            JSONObject rowData = (JSONObject) rowDataObj.getValue();
            if (MapUtils.isEmpty(rowData)) {
                continue;
            }
            JSONObject tbodyObj = new JSONObject();
            for (Map.Entry<String, Object> entry : rowData.entrySet()) {
                String key = entry.getKey();
                JSONObject value = (JSONObject) entry.getValue();
                if (value == null) {
                    continue;
                }
                String type = value.getString("type");
                if ("table".equals(type)) {
                    for (int i = 0; i < attributeList.size(); i++) {
                        JSONObject attributeObj = attributeList.getJSONObject(i);
                        if (MapUtils.isEmpty(attributeObj)) {
                            continue;
                        }
                        String attributeUuid = attributeObj.getString("attributeUuid");
                        if (!Objects.equals(attributeUuid, key)) {
                            continue;
                        }
                        JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                        if (MapUtils.isEmpty(attrConfig)) {
                            continue;
                        }
                        JSONObject valueObj = value.getJSONObject("value");
                        JSONObject tableObj = convertTableData(valueObj, attrConfig, null);
                        JSONObject cellObj = new JSONObject();
                        cellObj.put("text", tableObj);
                        cellObj.put("type", type);
                        cellObj.put("value", valueObj);
                        tbodyObj.put(key, cellObj);
                    }
                } else {
                    tbodyObj.put(key, value);
                }
            }
            if (CollectionUtils.isNotEmpty(selectUuidList)) {
                int index = selectUuidList.indexOf(rowUuid);
                if (index != -1) {
                    tbodyList.set(index, tbodyObj);
                }
            } else {
                tbodyList.add(tbodyObj);
            }
        }
        resultObj.put("theadList", theadList);
        resultObj.put("tbodyList", tbodyList);
        return resultObj;
    }

//    @Override
//    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
//        JSONObject resultObj = new JSONObject();
//        JSONArray dataObj = (JSONArray) attributeDataVo.getDataObj();
//        resultObj.put("value", dataObj);
//        if (CollectionUtils.isNotEmpty(dataObj)) {
//            JSONArray attributeList = configObj.getJSONArray("attributeList");
//            if (CollectionUtils.isNotEmpty(attributeList)) {
//                JSONArray theadList = new JSONArray();
//                for (int i = 0; i < attributeList.size(); i++) {
//                    JSONObject attributeObj = attributeList.getJSONObject(i);
//                    String attribute = attributeObj.getString("attribute");
//                    JSONObject theadObj = new JSONObject();
//                    theadObj.put("title", attribute);
//                    theadObj.put("key", attribute);
//                    theadList.add(theadObj);
//                }
//                JSONArray tbodyList = new JSONArray();
//                for (int i = 0; i < dataObj.size(); i++) {
//                    JSONObject tbodyObj = new JSONObject();
//                    JSONArray rowData = dataObj.getJSONArray(i);
//                    for (int j = 0; j < rowData.size(); j++) {
//                        JSONObject cellObj = new JSONObject();
//                        JSONObject attributeObj = attributeList.getJSONObject(j);
//                        String attribute = attributeObj.getString("attribute");
//                        String type = attributeObj.getString("type");
//                        cellObj.put("type", type);
//                        if ("text".equals(type)) {
//                            String value = rowData.getString(j);
//                            cellObj.put("value", value);
//                            cellObj.put("text", value);
//                        } else if ("textarea".equals(type)) {
//                            String value = rowData.getString(j);
//                            cellObj.put("value", value);
//                            cellObj.put("text", value);
//                        } else if ("select".equals(type)) {
//                            String value = rowData.getString(j);
//                            cellObj.put("value", value);
//                            String text = "";
//                            if (StringUtils.isNotBlank(value)) {
//                                JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
//                                String dataSource = attrConfig.getString("dataSource");
//                                if ("static".equals(dataSource)) {
//                                    JSONArray dataList = attrConfig.getJSONArray("dataList");
//                                    for (int k = 0; k < dataList.size(); k++) {
//                                        JSONObject data = dataList.getJSONObject(k);
//                                        String dataValue = data.getString("value");
//                                        if (dataValue.equals(value)) {
//                                            text = data.getString("text");
//                                        }
//                                    }
//                                } else if ("matrix".equals(dataSource)) {
//                                    String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
//                                    if (split.length == 2) {
//                                        text = split[1];
//                                    } else {
//                                        text = value;
//                                    }
//                                }
//                            }
//                            cellObj.put("text", text);
//                        } else if ("selects".equals(type)) {
//                            JSONArray valueList = rowData.getJSONArray(j);
//                            cellObj.put("value", valueList);
//                            JSONArray textList = new JSONArray();
//                            if (CollectionUtils.isNotEmpty(valueList)) {
//                                for (String value : valueList.toJavaList(String.class) ) {
//                                    String text = "";
//                                    if (StringUtils.isNotBlank(value)) {
//                                        JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
//                                        String dataSource = attrConfig.getString("dataSource");
//                                        if ("static".equals(dataSource)) {
//                                            JSONArray dataList = attrConfig.getJSONArray("dataList");
//                                            for (int k = 0; k < dataList.size(); k++) {
//                                                JSONObject data = dataList.getJSONObject(k);
//                                                String dataValue = data.getString("value");
//                                                if (dataValue.equals(value)) {
//                                                    text = data.getString("text");
//                                                }
//                                            }
//                                        } else if ("matrix".equals(dataSource)) {
//                                            String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
//                                            if (split.length == 2) {
//                                                text = split[1];
//                                            } else {
//                                                text = value;
//                                            }
//                                        }
//                                    }
//                                    textList.add(text);
//                                }
//                            }
//                            cellObj.put("text", textList);
//                        } else if ("radio".equals(type)) {
//                            String value = rowData.getString(j);
//                            cellObj.put("value", value);
//                            String text = "";
//                            if (StringUtils.isNotBlank(value)) {
//                                JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
//                                String dataSource = attrConfig.getString("dataSource");
//                                if ("static".equals(dataSource)) {
//                                    JSONArray dataList = attrConfig.getJSONArray("dataList");
//                                    for (int k = 0; k < dataList.size(); k++) {
//                                        JSONObject data = dataList.getJSONObject(k);
//                                        String dataValue = data.getString("value");
//                                        if (dataValue.equals(value)) {
//                                            text = data.getString("text");
//                                        }
//                                    }
//                                } else if ("matrix".equals(dataSource)) {
//                                    String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
//                                    if (split.length == 2) {
//                                        text = split[1];
//                                    } else {
//                                        text = value;
//                                    }
//                                }
//                            }
//                            cellObj.put("text", text);
//                        } else if ("checkbox".equals(type)) {
//                            JSONArray valueList = rowData.getJSONArray(j);
//                            cellObj.put("value", valueList);
//                            JSONArray textList = new JSONArray();
//                            if (CollectionUtils.isNotEmpty(valueList)) {
//                                for (String value : valueList.toJavaList(String.class) ) {
//                                    String text = "";
//                                    if (StringUtils.isNotBlank(value)) {
//                                        JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
//                                        String dataSource = attrConfig.getString("dataSource");
//                                        if ("static".equals(dataSource)) {
//                                            JSONArray dataList = attrConfig.getJSONArray("dataList");
//                                            for (int k = 0; k < dataList.size(); k++) {
//                                                JSONObject data = dataList.getJSONObject(k);
//                                                String dataValue = data.getString("value");
//                                                if (dataValue.equals(value)) {
//                                                    text = data.getString("text");
//                                                }
//                                            }
//                                        } else if ("matrix".equals(dataSource)) {
//                                            String[] split = value.split(IFormAttributeHandler.SELECT_COMPOSE_JOINER);
//                                            if (split.length == 2) {
//                                                text = split[1];
//                                            } else {
//                                                text = value;
//                                            }
//                                        }
//                                    }
//                                    textList.add(text);
//                                }
//                            }
//                            cellObj.put("text", textList);
//                        } else if ("date".equals(type)) {
//                            String value = rowData.getString(j);
//                            cellObj.put("value", value);
//                            cellObj.put("text", value);
//                        } else if ("time".equals(type)) {
//                            String value = rowData.getString(j);
//                            cellObj.put("value", value);
//                            cellObj.put("text", value);
//                        } else if ("table".equals(type)) {
//                            JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
//                            JSONArray valueList = rowData.getJSONArray(j);
//                            AttributeDataVo attributeData = new AttributeDataVo();
//                            attributeData.setDataObj(valueList);
//                            Object tableData = getMyDetailedData(attributeData, attrConfig);
//                            cellObj.put("value", valueList);
//                            cellObj.put("text", tableData);
//                        }
//                        tbodyObj.put(attribute, cellObj);
//                    }
//                    tbodyList.add(tbodyObj);
//                }
//                resultObj.put("theadList", theadList);
//                resultObj.put("tbodyList", tbodyList);
//            }
//        }
//        return resultObj;
//    }

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
        formAttributeVo.setMatrixUuidSet(matrixUuidSet);
        formAttributeVo.setMatrixUuidAttributeUuidSetMap(matrixUuidAttributeUuidSetMap);
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return getMyDetailedData(attributeDataVo, configObj);
    }

    @Override
    public int getExcelHeadLength(JSONObject configObj) {
        JSONArray attributeList = configObj.getJSONArray("attributeList");
        if (CollectionUtils.isNotEmpty(attributeList)) {
            return attributeList.size();
        }
        return 1;
    }

    @Override
    public int getExcelRowCount(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject detailedData = getMyDetailedData(attributeDataVo, configObj);
        if (MapUtils.isNotEmpty(detailedData)) {
            JSONArray tbodyList = detailedData.getJSONArray("tbodyList");
            if (CollectionUtils.isNotEmpty(tbodyList)) {
                return tbodyList.size() + 1;
            }
        }
        return 1;
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

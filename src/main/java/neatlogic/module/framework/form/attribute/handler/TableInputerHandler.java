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
import neatlogic.framework.form.attribute.core.FormAttributeDataConversionHandlerFactory;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.attribute.core.IFormAttributeDataConversionHandler;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.util.TableResultUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TableInputerHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMTABLEINPUTER.getHandler();
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
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        if (!attributeDataVo.dataIsEmpty()) {
            return "已更新";
        } else {
            return "";
        }
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        resultObj.remove("value");
        return resultObj;
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return null;
    }

    @Override
    public ParamType getParamType() {
        return null;
    }

    @Override
    public boolean isConditionable() {
        return false;
    }

    @Override
    public boolean isProcessTaskBatchSubmissionTemplateParam() {
        return false;
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return null;
    }

    /*
    表单组件配置信息
    {
        "handler": "formtableinputer",
        "reaction": {
            "hide": {},
            "readonly": {},
            "disable": {},
            "display": {},
            "emit": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-formstaticlist",
        "hasValue": true,
        "label": "表格输入_2",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "disableDefaultValue": true,
            "isMask": false,
            "isCanDrag": true,
            "width": "100%",
            "description": "",
            "dataConfig": [
                {
                    "handler": "formtext",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "文本框_1",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "cad032ca2175424eb815586694a8f17d",
                    "config": {
                        "isRequired": true,
                        "defaultValue": "文本1"
                    }
                },
                {
                    "handler": "formtextarea",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "文本域_2",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "bbc47193be544403a79f31e5529a93f4",
                    "config": {
                        "isRequired": true,
                        "defaultValue": "文本域1"
                    }
                },
                {
                    "handler": "formselect",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "下拉框_3",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "266abb12741f49218c6d6a6d1e89e131",
                    "config": {
                        "isRequired": true,
                        "defaultValue": "1",
                        "dataList": [
                            {
                                "text": "a1",
                                "value": "1"
                            },
                            {
                                "text": "a2",
                                "value": "2"
                            }
                        ],
                        "dataSource": "static"
                    }
                },
                {
                    "handler": "formradio",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "单选框_4",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "3805bbf114414604b6a514c6287b51f0",
                    "config": {
                        "isRequired": true,
                        "defaultValue": "3",
                        "dataList": [
                            {
                                "text": "a3",
                                "value": "3"
                            },
                            {
                                "text": "a4",
                                "value": "4"
                            }
                        ],
                        "dataSource": "static"
                    }
                },
                {
                    "handler": "formcheckbox",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "复选框_5",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "694181007eaa43079f0dba131593fe8a",
                    "config": {
                        "isRequired": true,
                        "defaultValue": [
                            "5",
                            "6"
                        ],
                        "dataList": [
                            {
                                "text": "a5",
                                "value": "5"
                            },
                            {
                                "text": "a6",
                                "value": "6"
                            }
                        ],
                        "isMultiple": true,
                        "dataSource": "static"
                    }
                },
                {
                    "handler": "formdate",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "日期_6",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "3c33ca77c2f148f4af50bf7f98715a38",
                    "config": {
                        "isRequired": true,
                        "defaultValue": "2023-03-02",
                        "format": "yyyy-MM-dd"
                    }
                },
                {
                    "handler": "formtime",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "时间_7",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "b9e04282e1f84b828d9ae745f58471fd",
                    "config": {
                        "isRequired": true,
                        "defaultValue": "00:00:00",
                        "format": "HH:mm:ss"
                    }
                },
                {
                    "handler": "formtable",
                    "reaction": {
                        "hide": {},
                        "readonly": {},
                        "disable": {},
                        "display": {},
                        "mask": {}
                    },
                    "isSearch": false,
                    "isExtra": true,
                    "hasValue": true,
                    "label": "表格_8",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "d2e965358f264cee926fe00b5caec36c",
                    "config": {
                        "isRequired": true,
                        "dataConfig": [
                            {
                                "handler": "formtext",
                                "reaction": {
                                    "hide": {},
                                    "readonly": {},
                                    "disable": {},
                                    "display": {},
                                    "mask": {}
                                },
                                "isSearch": false,
                                "isExtra": true,
                                "hasValue": true,
                                "label": "文本框_2",
                                "isSearchable": 0,
                                "uuid": "6e4161058e3043b38c334d36cb2cc1d4",
                                "config": {
                                    "isRequired": true,
                                    "defaultValue": "文本2"
                                }
                            }
                        ]
                    }
                }
            ],
            "isShowNumber": true,
            "lineNumber": 1,
            "isHide": false
        },
        "uuid": "476924a9d8ec4f06be393337f993a1cb"
    }
     */
    /*
    保存数据结构
    [
        {
            "bbc47193be544403a79f31e5529a93f4": "文本域1",
            "_selected": false,
            "266abb12741f49218c6d6a6d1e89e131": "1",
            "3805bbf114414604b6a514c6287b51f0": "3",
            "3c33ca77c2f148f4af50bf7f98715a38": "2023-03-02",
            "cad032ca2175424eb815586694a8f17d": "文本1",
            "d2e965358f264cee926fe00b5caec36c": [
                {
                    "_selected": false,
                    "6e4161058e3043b38c334d36cb2cc1d4": "文本2",
                    "uuid": "1a0339b98a104cae8240e50e51074508"
                }
            ],
            "b9e04282e1f84b828d9ae745f58471fd": "00:00:00",
            "694181007eaa43079f0dba131593fe8a": [
                "5",
                "6"
            ],
            "uuid": "d53aec8bea0943dcb721c70e0f1946f8"
        },
        {
            "bbc47193be544403a79f31e5529a93f4": "文本域1",
            "_selected": false,
            "266abb12741f49218c6d6a6d1e89e131": "1",
            "3805bbf114414604b6a514c6287b51f0": "3",
            "3c33ca77c2f148f4af50bf7f98715a38": "2023-03-02",
            "cad032ca2175424eb815586694a8f17d": "文本1",
            "d2e965358f264cee926fe00b5caec36c": [
                {
                    "_selected": false,
                    "6e4161058e3043b38c334d36cb2cc1d4": "文本2",
                    "uuid": "88225d931e564327bc9e472d953e7eb0"
                }
            ],
            "b9e04282e1f84b828d9ae745f58471fd": "00:00:00",
            "694181007eaa43079f0dba131593fe8a": [
                "5",
                "6"
            ],
            "uuid": "cef4da6d65e8476db2c64f1d5544557e"
        }
    ]
     */
    /*
    返回数据结构
    {
        "value": [
            {
                "266abb12741f49218c6d6a6d1e89e131": "1",
                "c2090bb8472d482eb8eaece9864c08f6": {"value": "6", "text": "许*航"},
                "3805bbf114414604b6a514c6287b51f0": "3",
                "3c33ca77c2f148f4af50bf7f98715a38": "2023-03-02",
                "cad032ca2175424eb815586694a8f17d": "文本1",
                "uuid": "227f0ef263ea4f27bc137b14e3a89539",
                "bbc47193be544403a79f31e5529a93f4": "文本域1",
                "_selected": false,
                "75ed2187390d4c4ea37dab180f6fee00": [
                    {"value": "6", "text": "金融同业部-同业业务中心"}
                ],
                "516fdf5d27e54fc2a8b7cf66bd02c8b9": [
                    {"value": "3333", "text": "薛*源"}
                ],
                "d2e965358f264cee926fe00b5caec36c": [
                    {
                        "_selected": false,
                        "6e4161058e3043b38c334d36cb2cc1d4": "文本2",
                        "uuid": "07336b8d9e9141b08d3b1150cc98d723"
                    }
                ],
                "b9e04282e1f84b828d9ae745f58471fd": "00:00:00",
                "694181007eaa43079f0dba131593fe8a": [
                    "5",
                    "6"
                ]
            },
            {
                "266abb12741f49218c6d6a6d1e89e131": "1",
                "c2090bb8472d482eb8eaece9864c08f6": {"value": "6", "text": "许*航"},
                "3805bbf114414604b6a514c6287b51f0": "3",
                "3c33ca77c2f148f4af50bf7f98715a38": "2023-03-02",
                "cad032ca2175424eb815586694a8f17d": "文本1",
                "uuid": "f78886aa137e4c4394992ae8c1de3dd8",
                "bbc47193be544403a79f31e5529a93f4": "文本域1",
                "_selected": false,
                "75ed2187390d4c4ea37dab180f6fee00": [
                    {"value": "6", "text": "金融同业部-同业业务中心"}
                ],
                "516fdf5d27e54fc2a8b7cf66bd02c8b9": [
                    {"value": "3333", "text": "薛*源"}
                ],
                "d2e965358f264cee926fe00b5caec36c": [
                    {
                        "_selected": false,
                        "6e4161058e3043b38c334d36cb2cc1d4": "文本2",
                        "uuid": "f502f52058ef4421ab002558b8cc1454"
                    }
                ],
                "b9e04282e1f84b828d9ae745f58471fd": "00:00:00",
                "694181007eaa43079f0dba131593fe8a": [
                    "5",
                    "6"
                ]
            }
        ],
        "theadList": [
            {
                "handler": "formtext",
                "title": "文本框_1",
                "key": "cad032ca2175424eb815586694a8f17d"
            },
            {
                "handler": "formtextarea",
                "title": "文本域_2",
                "key": "bbc47193be544403a79f31e5529a93f4"
            },
            {
                "handler": "formselect",
                "title": "下拉框_3",
                "key": "266abb12741f49218c6d6a6d1e89e131"
            },
            {
                "handler": "formselect",
                "title": "下拉多选_9",
                "key": "516fdf5d27e54fc2a8b7cf66bd02c8b9"
            },
            {
                "handler": "formradio",
                "title": "单选框_4",
                "key": "3805bbf114414604b6a514c6287b51f0"
            },
            {
                "handler": "formradio",
                "title": "单选框_10",
                "key": "c2090bb8472d482eb8eaece9864c08f6"
            },
            {
                "handler": "formcheckbox",
                "title": "复选框_5",
                "key": "694181007eaa43079f0dba131593fe8a"
            },
            {
                "handler": "formcheckbox",
                "title": "复选框_11",
                "key": "75ed2187390d4c4ea37dab180f6fee00"
            },
            {
                "handler": "formdate",
                "title": "日期_6",
                "key": "3c33ca77c2f148f4af50bf7f98715a38"
            },
            {
                "handler": "formtime",
                "title": "时间_7",
                "key": "b9e04282e1f84b828d9ae745f58471fd"
            },
            {
                "handler": "formtable",
                "title": "表格_8",
                "key": "d2e965358f264cee926fe00b5caec36c"
            }
        ],
        "tbodyList": [
            {
                "266abb12741f49218c6d6a6d1e89e131": "a1",
                "c2090bb8472d482eb8eaece9864c08f6": "许*航",
                "3805bbf114414604b6a514c6287b51f0": "a3",
                "3c33ca77c2f148f4af50bf7f98715a38": "2023-03-02",
                "cad032ca2175424eb815586694a8f17d": "文本1",
                "uuid": "227f0ef263ea4f27bc137b14e3a89539",
                "bbc47193be544403a79f31e5529a93f4": "文本域1",
                "_selected": false,
                "75ed2187390d4c4ea37dab180f6fee00": "金融同业部-同业业务中心",
                "516fdf5d27e54fc2a8b7cf66bd02c8b9": "薛*源",
                "d2e965358f264cee926fe00b5caec36c": {
                    "theadList": [
                        {
                            "handler": "formtext",
                            "title": "文本框_2",
                            "key": "6e4161058e3043b38c334d36cb2cc1d4"
                        }
                    ],
                    "tbodyList": [
                        {
                            "_selected": false,
                            "6e4161058e3043b38c334d36cb2cc1d4": "文本2",
                            "uuid": "07336b8d9e9141b08d3b1150cc98d723"
                        }
                    ]
                },
                "b9e04282e1f84b828d9ae745f58471fd": "00:00:00",
                "694181007eaa43079f0dba131593fe8a": "a5、a6"
            },
            {
                "266abb12741f49218c6d6a6d1e89e131": "a1",
                "c2090bb8472d482eb8eaece9864c08f6": "许*航",
                "3805bbf114414604b6a514c6287b51f0": "a3",
                "3c33ca77c2f148f4af50bf7f98715a38": "2023-03-02",
                "cad032ca2175424eb815586694a8f17d": "文本1",
                "uuid": "f78886aa137e4c4394992ae8c1de3dd8",
                "bbc47193be544403a79f31e5529a93f4": "文本域1",
                "_selected": false,
                "75ed2187390d4c4ea37dab180f6fee00": "金融同业部-同业业务中心",
                "516fdf5d27e54fc2a8b7cf66bd02c8b9": "薛*源",
                "d2e965358f264cee926fe00b5caec36c": {
                    "theadList": [
                        {
                            "handler": "formtext",
                            "title": "文本框_2",
                            "key": "6e4161058e3043b38c334d36cb2cc1d4"
                        }
                    ],
                    "tbodyList": [
                        {
                            "_selected": false,
                            "6e4161058e3043b38c334d36cb2cc1d4": "文本2",
                            "uuid": "f502f52058ef4421ab002558b8cc1454"
                        }
                    ]
                },
                "b9e04282e1f84b828d9ae745f58471fd": "00:00:00",
                "694181007eaa43079f0dba131593fe8a": "a5、a6"
            }
        ]
    }
     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        JSONArray dataArray = (JSONArray) attributeDataVo.getDataObj();
        resultObj.put("value", dataArray);
        if (CollectionUtils.isEmpty(dataArray)) {
            return resultObj;
        }
        JSONArray dataConfig = configObj.getJSONArray("dataConfig");
        if (CollectionUtils.isEmpty(dataConfig)) {
            return resultObj;
        }
        JSONObject tableObj = convertTableData(dataArray, dataConfig);
        resultObj.putAll(tableObj);
        return resultObj;
    }

    /**
     * 将原始数据转换成表格数据（包含theadList、tbodyList）
     * @param dataArray 原始数据
     * @param dataConfig 配置信息
     * @return
     */
    private JSONObject convertTableData(JSONArray dataArray, JSONArray dataConfig) {
        JSONArray theadList = new JSONArray();
        Map<String, String> attributeHandlerMap = new HashMap<>();
        Map<String, JSONObject> attributeConfigMap = new HashMap<>();
        for (int i = 0; i < dataConfig.size(); i++) {
            JSONObject attributeObj = dataConfig.getJSONObject(i);
            String uuid = attributeObj.getString("uuid");
            String label = attributeObj.getString("label");
            String handler = attributeObj.getString("handler");
            JSONObject config = attributeObj.getJSONObject("config");
            attributeConfigMap.put(uuid, config);
            attributeHandlerMap.put(uuid, handler);
            JSONObject theadObj = new JSONObject();
            theadObj.put("title", label);
            theadObj.put("handler", handler);
            theadObj.put("key", uuid);
            theadList.add(theadObj);
        }
        // 不需要解析的属性类型，有文本框、文本域、时间、日期
        List<String> noNeedParseHandlerList = new ArrayList<>();
        noNeedParseHandlerList.add(FormHandler.FORMTEXT.getHandler());
        noNeedParseHandlerList.add(FormHandler.FORMTEXTAREA.getHandler());
        noNeedParseHandlerList.add(FormHandler.FORMTIME.getHandler());
        noNeedParseHandlerList.add(FormHandler.FORMDATE.getHandler());

        // 需要解析的属性类型，有下拉框、单选框、复选框
        List<String> needParseHandlerList = new ArrayList<>();
        needParseHandlerList.add(FormHandler.FORMSELECT.getHandler());
        needParseHandlerList.add(FormHandler.FORMRADIO.getHandler());
        needParseHandlerList.add(FormHandler.FORMCHECKBOX.getHandler());

        JSONArray tbodyList = new JSONArray();
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject newRowDataObj = new JSONObject();
            JSONObject rowDataObj = dataArray.getJSONObject(i);
            for (Map.Entry<String, Object> cellDataObj : rowDataObj.entrySet()) {
                String key = cellDataObj.getKey();
                String handler = attributeHandlerMap.get(key);
                if (noNeedParseHandlerList.contains(handler)) {
                    newRowDataObj.put(key, cellDataObj.getValue());
                } else if (needParseHandlerList.contains(handler)) {
                    IFormAttributeDataConversionHandler formAttributeDataConversionHandler = FormAttributeDataConversionHandlerFactory.getHandler(handler);
                    if (formAttributeDataConversionHandler != null) {
                        JSONObject config = attributeConfigMap.get(key);
                        AttributeDataVo attributeDataVo = new AttributeDataVo();
                        attributeDataVo.setDataObj(cellDataObj.getValue());
                        Object result = formAttributeDataConversionHandler.dataTransformationForEmail(attributeDataVo, config);
                        newRowDataObj.put(key, result);
                    } else {
                        newRowDataObj.put(key, "");
                    }
                } else if (Objects.equals("formtable", handler)) {
                    JSONObject config = attributeConfigMap.get(key);
                    // 属性类型是表格时，递归调用convertTableData方法解析
                    JSONObject tableObj = convertTableData((JSONArray) cellDataObj.getValue(), config.getJSONArray("dataConfig"));
                    newRowDataObj.put(key, tableObj);
                } else {
                    // 未来增加属性类型，不做解析
                    newRowDataObj.put(key, cellDataObj.getValue());
                }
            }
            tbodyList.add(newRowDataObj);
        }
        return TableResultUtil.getResult(theadList, tbodyList);
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
                if (MapUtils.isEmpty(attributeObj)) {
                    continue;
                }
                JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                if (MapUtils.isEmpty(attrConfig)) {
                    continue;
                }
                String type = attributeObj.getString("type");
                if ("table".equals(type)) {
                    JSONArray attributeList = attrConfig.getJSONArray("attributeList");
                    if (CollectionUtils.isEmpty(attributeList)) {
                        continue;
                    }
                    for (int j = 0; j < attributeList.size(); j++) {
                        JSONObject attribute = attributeList.getJSONObject(j);
                        if (MapUtils.isEmpty(attribute)) {
                            continue;
                        }
                        JSONObject attrConfigObj = attribute.getJSONObject("attrConfig");
                        if (MapUtils.isEmpty(attrConfigObj)) {
                            continue;
                        }
                        String dataSource = attrConfigObj.getString("dataSource");
                        if ("matrix".equals(dataSource)) {
                            parseExtendAttribute(attrConfigObj, matrixUuidSet, matrixUuidAttributeUuidSetMap);
                        }
                    }
                } else {
                    String dataSource = attrConfig.getString("dataSource");
                    if ("matrix".equals(dataSource)) {
                        parseExtendAttribute(attrConfig, matrixUuidSet, matrixUuidAttributeUuidSetMap);
                    }
                }
            }
        }
        formAttributeVo.setMatrixUuidSet(matrixUuidSet);
        formAttributeVo.setMatrixUuidAttributeUuidSetMap(matrixUuidAttributeUuidSetMap);
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        resultObj.remove("value");
        return resultObj;
    }

    @Override
    public int getExcelHeadLength(JSONObject configObj) {
        int count = 0;
        JSONArray dataConfig = configObj.getJSONArray("dataConfig");
        if (CollectionUtils.isNotEmpty(dataConfig)) {
            for (int i = 0; i < dataConfig.size(); i++) {
                JSONObject columnHeadObj = dataConfig.getJSONObject(i);
                Boolean isPC = columnHeadObj.getBoolean("isPC");
                if (!Objects.equals(isPC, true)) {
                    continue;
                }
                String uuid = columnHeadObj.getString("uuid");
                if (StringUtils.isBlank(uuid)) {
                    continue;
                }
                count++;
            }
        }
        if (count == 0) {
            count++;
        }
        return count;
    }

    @Override
    public int getExcelRowCount(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONArray dataArray = (JSONArray) attributeDataVo.getDataObj();
        if (CollectionUtils.isNotEmpty(dataArray)) {
            return dataArray.size() + 1;
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

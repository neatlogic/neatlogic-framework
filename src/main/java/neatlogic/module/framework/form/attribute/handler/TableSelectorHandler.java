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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TableSelectorHandler extends FormHandlerBase {

    private final static Logger logger = LoggerFactory.getLogger(TableSelectorHandler.class);

    @Override
    public String getHandler() {
        return FormHandler.FORMTABLESELECTOR.getHandler();
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
    {
        "handler": "formtableselector",
        "reaction": {
            "filter": {},
            "hide": {},
            "readonly": {},
            "disable": {},
            "display": {},
            "emit": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-formdynamiclist",
        "hasValue": true,
        "label": "表格选择_3",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "disableDefaultValue": true,
            "mapping": {},
            "description": "",
            "pageSize": 10,
            "matrixType": "custom",
            "dataConfig": [
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "文本框1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "a22002af151b4da589bc390f3ad164f5"
                },
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "下拉框1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "579c9de2986d48738cc0ecafab2f43d3"
                },
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "日期1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "b67e647f6fd049e59cc903fb678c7291"
                },
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "用户1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "e00fc42fca5d4f5e831d296ed68b3ff1"
                },
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "分组1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "14a67eabc2584d4da19c561c26ed0f3a"
                },
                {
                    "handler": "formtext",
                    "isSearch": false,
                    "hasValue": true,
                    "label": "角色1",
                    "isMobile": false,
                    "isSearchable": 1,
                    "isPC": true,
                    "uuid": "93303dc9e96348e68ae5009702958606"
                },
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
                    "label": "扩展属性_1",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "147321ac250d48ed9cff09516f47bd8f",
                    "config": {
                        "isRequired": false,
                        "isMask": false,
                        "isHide": false
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
                    "label": "扩展属性_2",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "688dfb6ec05147098cf8e34a1141ad58",
                    "config": {
                        "mapping": {
                            "text": "e00fc42fca5d4f5e831d296ed68b3ff1",
                            "value": "a22002af151b4da589bc390f3ad164f5"
                        },
                        "isMultiple": true,
                        "dataSource": "matrix",
                        "matrixUuid": "e54221ef3b814eebbf57df252426923c"
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
                    "label": "扩展属性_3",
                    "isMobile": false,
                    "isSearchable": 0,
                    "isPC": true,
                    "uuid": "42dd19dc6a234275bd8d2585d4c2a751",
                    "config": {
                        "dataList": [
                            {
                                "text": "radio1",
                                "value": "1"
                            },
                            {
                                "text": "radio2",
                                "value": "2"
                            }
                        ],
                        "isMultiple": false,
                        "dataSource": "static"
                    }
                }
            ],
            "matrixUuid": "e54221ef3b814eebbf57df252426923c",
            "isHide": false,
            "mode": "dialog",
            "needPage": true,
            "isMask": false,
            "sourceColumnList": [],
            "width": "100%",
            "dataSource": "matrix"
        },
        "uuid": "db70eeb48a144616ab038180e32eea5b"
    }
     */
    /*
    [
        {
            "93303dc9e96348e68ae5009702958606": "R_职位_科技运营中心经理",
            "_selected": false,
            "e00fc42fca5d4f5e831d296ed68b3ff1": "0093a20ff182675fee7b6f821b874488",
            "14a67eabc2584d4da19c561c26ed0f3a": "金融市场总部",
            "a22002af151b4da589bc390f3ad164f5": "2",
            "579c9de2986d48738cc0ecafab2f43d3": "a2",
            "42dd19dc6a234275bd8d2585d4c2a751": "1",
            "b67e647f6fd049e59cc903fb678c7291": "2022-09-24 00:00:00",
            "147321ac250d48ed9cff09516f47bd8f": "扩展属性_1_3",
            "688dfb6ec05147098cf8e34a1141ad58": [
                {"value": "6", "text": "许*航"}
            ],
            "uuid": "1b0661a40da3478c9be344c99db2ffb0"
        },
        {
            "93303dc9e96348e68ae5009702958606": "测试用户功能",
            "_selected": false,
            "e00fc42fca5d4f5e831d296ed68b3ff1": "薛*源",
            "14a67eabc2584d4da19c561c26ed0f3a": "纪律监督室",
            "a22002af151b4da589bc390f3ad164f5": "3333",
            "579c9de2986d48738cc0ecafab2f43d3": "a2",
            "42dd19dc6a234275bd8d2585d4c2a751": "2",
            "b67e647f6fd049e59cc903fb678c7291": "2022-12-13 00:00:00",
            "147321ac250d48ed9cff09516f47bd8f": "扩展属性_1_2",
            "688dfb6ec05147098cf8e34a1141ad58": [
                {"value": "3333", "text": "薛*源"}
            ],
            "uuid": "8eed18cdbcb34691b379d1b5fc1e3b0f"
        },
        {
            "93303dc9e96348e68ae5009702958606": "测试用户功能",
            "_selected": false,
            "e00fc42fca5d4f5e831d296ed68b3ff1": "冉ss",
            "14a67eabc2584d4da19c561c26ed0f3a": "基金公司（筹建）",
            "a22002af151b4da589bc390f3ad164f5": "1",
            "579c9de2986d48738cc0ecafab2f43d3": "a1",
            "42dd19dc6a234275bd8d2585d4c2a751": "1",
            "b67e647f6fd049e59cc903fb678c7291": "2023-02-10 00:00:00",
            "147321ac250d48ed9cff09516f47bd8f": "扩展属性_1_1",
            "688dfb6ec05147098cf8e34a1141ad58": [
                {"value": "3", "text": "冉ss"},
                {"value": "6", "text": "许*航"}
            ],
            "uuid": "c8bc60ca9ec740d7a8808753ab504bab"
        }
    ]
     */
    /*
    {
        "value": [
            {
                "93303dc9e96348e68ae5009702958606": "R_职位_科技运营中心经理",
                "e00fc42fca5d4f5e831d296ed68b3ff1": "0093a20ff182675fee7b6f821b874488",
                "14a67eabc2584d4da19c561c26ed0f3a": "金融市场总部",
                "c95435c411884f149e8e22a6e67ab1ec": [
                    {"value": "6", "text": "许*航"}
                ],
                "a22002af151b4da589bc390f3ad164f5": "2",
                "579c9de2986d48738cc0ecafab2f43d3": "a2",
                "b67e647f6fd049e59cc903fb678c7291": "2022-09-24 00:00:00",
                "isSelected": true,
                "ccdbb7c0610f441994c4ab2869bd9f1a": "1",
                "uuid": "1b0661a40da3478c9be344c99db2ffb0",
                "bab495ec4ada43feb1d8e11d09fb5d66": "扩展属性_1_4"
            },
            {
                "93303dc9e96348e68ae5009702958606": "测试用户功能",
                "e00fc42fca5d4f5e831d296ed68b3ff1": "薛*源",
                "14a67eabc2584d4da19c561c26ed0f3a": "纪律监督室",
                "c95435c411884f149e8e22a6e67ab1ec": [
                    {"value": "3", "text": "冉ss"}
                ],
                "a22002af151b4da589bc390f3ad164f5": "3333",
                "579c9de2986d48738cc0ecafab2f43d3": "a2",
                "b67e647f6fd049e59cc903fb678c7291": "2022-12-13 00:00:00",
                "isSelected": true,
                "ccdbb7c0610f441994c4ab2869bd9f1a": "2",
                "uuid": "8eed18cdbcb34691b379d1b5fc1e3b0f",
                "bab495ec4ada43feb1d8e11d09fb5d66": "扩展属性_1_3"
            }
        ],
        "theadList": [
            {
                "handler": "formtext",
                "title": "文本框1",
                "key": "a22002af151b4da589bc390f3ad164f5"
            },
            {
                "handler": "formtext",
                "title": "下拉框1",
                "key": "579c9de2986d48738cc0ecafab2f43d3"
            },
            {
                "handler": "formtext",
                "title": "日期1",
                "key": "b67e647f6fd049e59cc903fb678c7291"
            },
            {
                "handler": "formtext",
                "title": "用户1",
                "key": "e00fc42fca5d4f5e831d296ed68b3ff1"
            },
            {
                "handler": "formtext",
                "title": "分组1",
                "key": "14a67eabc2584d4da19c561c26ed0f3a"
            },
            {
                "handler": "formtext",
                "title": "角色1",
                "key": "93303dc9e96348e68ae5009702958606"
            },
            {
                "handler": "formtext",
                "title": "扩展属性_1",
                "key": "bab495ec4ada43feb1d8e11d09fb5d66"
            },
            {
                "handler": "formselect",
                "title": "扩展属性_2",
                "key": "c95435c411884f149e8e22a6e67ab1ec"
            },
            {
                "handler": "formradio",
                "title": "扩展属性_3",
                "key": "ccdbb7c0610f441994c4ab2869bd9f1a"
            }
        ],
        "tbodyList": [
            {
                "93303dc9e96348e68ae5009702958606": "R_职位_科技运营中心经理",
                "e00fc42fca5d4f5e831d296ed68b3ff1": "0093a20ff182675fee7b6f821b874488",
                "14a67eabc2584d4da19c561c26ed0f3a": "金融市场总部",
                "c95435c411884f149e8e22a6e67ab1ec": "许*航",
                "a22002af151b4da589bc390f3ad164f5": "2",
                "579c9de2986d48738cc0ecafab2f43d3": "a2",
                "b67e647f6fd049e59cc903fb678c7291": "2022-09-24 00:00:00",
                "isSelected": true,
                "ccdbb7c0610f441994c4ab2869bd9f1a": "radio1",
                "uuid": "1b0661a40da3478c9be344c99db2ffb0",
                "bab495ec4ada43feb1d8e11d09fb5d66": "扩展属性_1_4"
            },
            {
                "93303dc9e96348e68ae5009702958606": "测试用户功能",
                "e00fc42fca5d4f5e831d296ed68b3ff1": "薛*源",
                "14a67eabc2584d4da19c561c26ed0f3a": "纪律监督室",
                "c95435c411884f149e8e22a6e67ab1ec": "冉ss",
                "a22002af151b4da589bc390f3ad164f5": "3333",
                "579c9de2986d48738cc0ecafab2f43d3": "a2",
                "b67e647f6fd049e59cc903fb678c7291": "2022-12-13 00:00:00",
                "isSelected": true,
                "ccdbb7c0610f441994c4ab2869bd9f1a": "radio2",
                "uuid": "8eed18cdbcb34691b379d1b5fc1e3b0f",
                "bab495ec4ada43feb1d8e11d09fb5d66": "扩展属性_1_3"
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
        Map<String, String> extraAttributeHandlerMap = new HashMap<>();
        Map<String, JSONObject> extraAttributeConfigMap = new HashMap<>();
        for (int i = 0; i < dataConfig.size(); i++) {
            JSONObject attributeObj = dataConfig.getJSONObject(i);
            String uuid = attributeObj.getString("uuid");
            String label = attributeObj.getString("label");
            String handler = attributeObj.getString("handler");
            Boolean isExtra = attributeObj.getBoolean("isExtra");
            if (Objects.equals(isExtra, true)) {
                JSONObject config = attributeObj.getJSONObject("config");
                extraAttributeConfigMap.put(uuid, config);
                extraAttributeHandlerMap.put(uuid, handler);
            }
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
                String handler = extraAttributeHandlerMap.get(key);
                if (StringUtils.isBlank(handler)) {
                    // 非扩展属性
                    newRowDataObj.put(key, cellDataObj.getValue());
                } else if (noNeedParseHandlerList.contains(handler)) {
                    newRowDataObj.put(key, cellDataObj.getValue());
                } else if (needParseHandlerList.contains(handler)) {
                    IFormAttributeDataConversionHandler formAttributeDataConversionHandler = FormAttributeDataConversionHandlerFactory.getHandler(handler);
                    if (formAttributeDataConversionHandler != null) {
                        JSONObject config = extraAttributeConfigMap.get(key);
                        AttributeDataVo attributeDataVo = new AttributeDataVo();
                        attributeDataVo.setDataObj(cellDataObj.getValue());
                        Object result = formAttributeDataConversionHandler.dataTransformationForEmail(attributeDataVo, config);
                        newRowDataObj.put(key, result);
                    } else {
                        newRowDataObj.put(key, "");
                    }
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
        Set<String> integrationUuidSet = new HashSet<>();
        Set<String> matrixUuidSet = new HashSet<>();
        Map<String, Set<String>> matrixUuidAttributeUuidSetMap = new HashMap<>();
        JSONObject config = formAttributeVo.getConfig();
        String dataSource = config.getString("dataSource");
        if ("matrix".equals(dataSource)) {
            String matrixUuid = config.getString("matrixUuid");
            if (StringUtils.isNotBlank(matrixUuid)) {
                Set<String> attributeUuidSet = new HashSet<>();
                matrixUuidSet.add(matrixUuid);
                /** 矩阵属性 **/
                JSONArray dataConfig = config.getJSONArray("dataConfig");
                if (CollectionUtils.isNotEmpty(dataConfig)) {
                    for (int i = 0; i < dataConfig.size(); i++) {
                        JSONObject attributeObj = dataConfig.getJSONObject(i);
                        if (MapUtils.isNotEmpty(attributeObj)) {
                            String uuid = attributeObj.getString("uuid");
                            if (StringUtils.isNotBlank(uuid)) {
                                attributeUuidSet.add(uuid);
                            }
                        }
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
                /** 扩展属性 **/
                JSONArray attributeArray = config.getJSONArray("attributeList");
                if (CollectionUtils.isNotEmpty(attributeArray)) {
                    for (int i = 0; i < attributeArray.size(); i++) {
                        JSONObject attributeObj = attributeArray.getJSONObject(i);
                        if (MapUtils.isNotEmpty(attributeObj)) {
                            JSONObject attrConfig = attributeObj.getJSONObject("attrConfig");
                            if (MapUtils.isNotEmpty(attrConfig)) {
                                dataSource = attrConfig.getString("dataSource");
                                if ("matrix".equals(dataSource)) {
                                    matrixUuid = attrConfig.getString("matrixUuid");
                                    if (StringUtils.isNotBlank(matrixUuid)) {
                                        attributeUuidSet = new HashSet<>();
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
                        }
                    }
                }
            }
        } else if ("integration".equals(dataSource)) {
            String integrationUuid = config.getString("integrationUuid");
            if (StringUtils.isNotBlank(integrationUuid)) {
                integrationUuidSet.add(integrationUuid);
            }
        }

        formAttributeVo.setMatrixUuidSet(matrixUuidSet);
        formAttributeVo.setMatrixUuidAttributeUuidSetMap(matrixUuidAttributeUuidSetMap);
        formAttributeVo.setIntegrationUuidSet(integrationUuidSet);
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
}

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
import neatlogic.framework.util.FormUtil;
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
    public Object conversionDataType(Object source, String attributeLabel) {
        if (source == null) {
            return null;
        }
        if (source instanceof JSONObject) {
            return source;
        } else if (source instanceof String) {
            String sourceStr = (String) source;
            if (sourceStr.startsWith("{") && sourceStr.endsWith("}")) {
                try {
                    return JSONObject.parseObject(sourceStr);
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
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            return textArray;
        }
        return resultObj.getJSONArray("valueList");
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
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
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

    /*
    表单组件配置信息
    {
        "handler": "formradio",
        "reaction": {
            "filter": {},
            "hide": {},
            "readonly": {},
            "setvalue": {},
            "disable": {},
            "display": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-circle-o",
        "hasValue": true,
        "label": "单选框_2",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "mapping": {
                "text": "14a67eabc2584d4da19c561c26ed0f3a",
                "value": "e00fc42fca5d4f5e831d296ed68b3ff1"
            },
            "defaultValue": "",
            "description": "",
            "isMultiple": false,
            "matrixUuid": "e54221ef3b814eebbf57df252426923c",
            "isHide": false,
            "isMask": false,
            "isReadOnly": false,
            "sourceColumnList": [],
            "dataList": [],
            "width": "100%",
            "isDisabled": false,
            "defaultValueType": "self",
            "dataSource": "matrix",
            "direction": "vertical"
        },
        "uuid": "17acdaf2693441d4aa0414645db0cd64",
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

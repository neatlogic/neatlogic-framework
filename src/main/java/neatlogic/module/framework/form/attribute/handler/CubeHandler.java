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
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CubeHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMCUBE.getHandler();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return null;
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
        return "已更新";
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            List<String> textList = new ArrayList<>();
            for (int i = 0; i < textArray.size(); i++) {
                JSONObject textObj = textArray.getJSONObject(i);
                String type = textObj.getString("type");
                String option = textObj.getString("option");
                textList.add(type + "-" + option);
            }
            return String.join("、", textList);
        }
        return null;
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return null;
    }

    @Override
    public ParamType getParamType() {
        return ParamType.ARRAY;
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

    /*
    表单组件配置信息
    {
        "handler": "formcube",
        "reaction": {
            "hide": {},
            "readonly": {},
            "disable": {},
            "display": {},
            "emit": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-softwareservice",
        "hasValue": true,
        "label": "矩阵选择_8",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "isMask": false,
            "isReadOnly": false,
            "typeList": [
                {
                    "text": "a4",
                    "value": "4"
                },
                {
                    "text": "a5",
                    "value": "5"
                },
                {
                    "text": "a6",
                    "value": "6"
                }
            ],
            "width": "100%",
            "optionList": [
                {
                    "text": "b4",
                    "value": "4"
                },
                {
                    "text": "b5",
                    "value": "5"
                },
                {
                    "text": "b6",
                    "value": "6"
                }
            ],
            "description": "",
            "isMultiple": true,
            "isDisabled": false,
            "isHide": false
        },
        "uuid": "df9d1c8df6cf49789be7abeaaaf0c203"
    }
     */
    /*
    保存数据结构
    [{"type":"2","option":"2"}]
     */
    /*
    返回数据结构

     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj == null) {
            return resultObj;
        }
        JSONArray valueList = (JSONArray) dataObj;
        if (CollectionUtils.isEmpty(valueList)) {
            return resultObj;
        }
        JSONArray typeArray = configObj.getJSONArray("typeList");
        if (CollectionUtils.isEmpty(typeArray)) {
            return resultObj;
        }
        JSONArray optionArray = configObj.getJSONArray("optionList");
        if (CollectionUtils.isEmpty(optionArray)) {
            return resultObj;
        }
        List<ValueTextVo> typeList = typeArray.toJavaList(ValueTextVo.class);
        Map<Object, String> typeMap = typeList.stream().collect(Collectors.toMap(e -> e.getValue(), e -> e.getText()));
        List<ValueTextVo> optionList = optionArray.toJavaList(ValueTextVo.class);
        Map<Object, String> optionMap = optionList.stream().collect(Collectors.toMap(e -> e.getValue(), e -> e.getText()));
        JSONArray textList = new JSONArray();
        for (int i = 0; i < valueList.size(); i++) {
            JSONObject valueObj = valueList.getJSONObject(i);
            if (MapUtils.isEmpty(valueObj)) {
                continue;
            }
            String type = valueObj.getString("type");
            if (StringUtils.isBlank(type)) {
                continue;
            }
            String option = valueObj.getString("option");
            if (StringUtils.isBlank(option)) {
                continue;
            }
            String typeText = typeMap.get(type);
            String optionText = optionMap.get(option);
            JSONObject textObj = new JSONObject();
            textObj.put("type", typeText);
            textObj.put("option", optionText);
            textList.add(textObj);
        }
        resultObj.put("valueList", valueList);
        resultObj.put("textList", textList);
        return resultObj;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = getMyDetailedData(attributeDataVo, configObj);
        JSONArray textArray = resultObj.getJSONArray("textList");
        if (CollectionUtils.isNotEmpty(textArray)) {
            List<String> textList = new ArrayList<>();
            for (int i = 0; i < textArray.size(); i++) {
                JSONObject textObj = textArray.getJSONObject(i);
                String type = textObj.getString("type");
                String option = textObj.getString("option");
                textList.add(type + "-" + option);
            }
            return String.join(",", textList);
        }
        JSONArray valueArray = resultObj.getJSONArray("valueList");
        if (CollectionUtils.isNotEmpty(valueArray)) {
            List<String> valueList = new ArrayList<>();
            for (int i = 0; i < valueArray.size(); i++) {
                JSONObject valueObj = valueArray.getJSONObject(i);
                String type = valueObj.getString("type");
                String option = valueObj.getString("option");
                valueList.add(type + "-" + option);
            }
            return String.join(",", valueList);
        }
        return null;
    }
}

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

import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.form.attribute.core.FormHandlerBase;

@Component
public class TimeHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMTIME.getHandler();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "time";
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public int getSort() {
        return 7;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getDataObj();
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getDataObj();
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return text;
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMTIME.getHandlerName();
    }

    @Override
    public String getIcon() {
        return "tsfont-formtime";
    }

    @Override
    public ParamType getParamType() {
        return ParamType.DATE;
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

    /*
    ????????????????????????
    {
        "handler": "formtime",
        "reaction": {
            "hide": {},
            "readonly": {},
            "setvalue": {},
            "disable": {},
            "display": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-formtime",
        "hasValue": true,
        "label": "??????_5",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "isMask": false,
            "isReadOnly": false,
            "width": "100%",
            "format": "HH:mm:ss",
            "description": "",
            "validValueList": [],
            "placeholder": "???????????????",
            "isDisabled": false,
            "isHide": false
        },
        "uuid": "65a008fda8804977a0037ceca868f9d4"
    }
     */
    /*
    ??????????????????
    "05:05:05"
     */
    /*
    ??????????????????
    {
        "format": "HH:mm:ss",
        "value": "05:05:05"
    }
     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        resultObj.put("format", configObj.getString("format"));
        resultObj.put("value", attributeDataVo.getDataObj());
        return resultObj;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getDataObj();
    }
}

/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

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
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CkEditorHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMCKEDITOR.getHandler();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "input";//富文本不管是哪种模式下过滤都是input
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public Object conversionDataType(Object source, String attributeLabel) {
        return convertToString(source, attributeLabel);
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
    public ParamType getParamType() {
        return ParamType.STRING;
    }

    @Override
    public boolean isConditionable() {
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
    public Boolean isUseFormConfig() {
        return false;
    }

    /*
    表单组件配置信息
    {
        "handler": "formckeditor",
        "reaction": {
            "hide": {},
            "readonly": {},
            "setvalue": {},
            "disable": {},
            "display": {},
            "emit": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-script",
        "hasValue": true,
        "label": "富文本框_12",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "isMask": false,
            "isReadOnly": false,
            "width": "100%",
            "description": "",
            "isDisabled": false,
            "isHide": false
        },
        "uuid": "83e84a3da13d46f78a204bf855663c9a",
        "switchHandler": [
            "formtext",
            "formtextarea",
            "formckeditor",
            "formnumber",
            "formpassword"
        ]
    }
     */
    /*
    保存数据结构
    "文本c"
     */
    /*
    返回数据结构
    {
        "value": "文本c"
    }
     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        resultObj.put("value", attributeDataVo.getDataObj());
        return resultObj;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getDataObj();
    }
}

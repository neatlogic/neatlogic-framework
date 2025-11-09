/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.form.attribute.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PasswordHandler extends FormHandlerBase {

//    public final static String PASSWORD_MASK_SYMBOL = "******";

    @Override
    public String getHandler() {
        return FormHandler.FORMPASSWORD.getHandler();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "input";
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        String dataObj = (String) attributeDataVo.getDataObj();
        if (StringUtils.isNotBlank(dataObj)) {
            // 加密
            attributeDataVo.setDataObj(RC4Util.encrypt(dataObj));
        }
        return null;
    }

    @Override
    public Object conversionDataType(Object source, String attributeLabel) {
        return convertToString(source, attributeLabel);
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return "已更新";
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        Object dataObj = attributeDataVo.getDataObj();
        if (dataObj != null) {
            String data = (String) dataObj;
            if (StringUtils.isNotBlank(data)) {
                return StringUtils.repeat("*", 8);
            }
        }
        return dataObj;
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

    /*
    表单组件配置信息
    {
        "handler": "formpassword",
        "reaction": {
            "hide": {},
            "readonly": {},
            "disable": {},
            "display": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-option-horizontal",
        "hasValue": true,
        "label": "密码_4",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "isMask": false,
            "width": "100%",
            "description": "",
            "isHide": false
        },
        "uuid": "4c0f58dc583c4fb0a3e5ff3e10b75797",
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
    "12345"
     */
    /*
    返回数据结构
    {
        "value": "12345"
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

    @Override
    public Object passwordEncryption(Object source, JSONObject configObj) {// , Object oldSource
        if (source == null) {
            return null;
        }
        if (source instanceof String) {
//            if (Objects.equals(source, PASSWORD_MASK_SYMBOL)) {
//                if (oldSource != null) {
//                    return oldSource;
//                }
//            }
            if (StringUtils.isNotBlank((String) source)) {
                return RC4Util.encrypt((String) source);
            }
        }
        return source;
    }

//    @Override
//    public String passwordDecryption(Object source, JSONObject configObj, String attributeUuid, JSONObject otherParamConfig) {
//        if (source == null) {
//            return null;
//        }
//        if (source instanceof String) {
//            return RC4Util.decrypt((String) source);
//        }
//        return null;
//    }

    @Override
    public JSONObject passwordDecryption(Object source, String attributeUuid, JSONObject otherParamConfig) {
        JSONObject resultObj = new JSONObject();
        if (source == null) {
            return resultObj;
        }
        if (source instanceof String) {
            String password = RC4Util.decrypt((String) source);
            resultObj.put("password", password);
        }
        return resultObj;
    }

//    @Override
//    public Object passwordMask(Object source, JSONObject configObj) {
//        if (source == null) {
//            return null;
//        }
//        return PASSWORD_MASK_SYMBOL;
//    }
}

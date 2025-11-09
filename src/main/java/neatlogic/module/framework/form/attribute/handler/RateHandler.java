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
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.form.exception.FormExtendAttributeConfigIllegalException;
import org.springframework.stereotype.Component;

@Component
public class RateHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMRATE.getHandler();
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
        if (source == null) {
            return null;
        }
        if (source instanceof String) {
            try {
                return Integer.valueOf((String) source);
            } catch (NumberFormatException e) {
            }
        } else if (source instanceof Number) {
            return source;
        }
        throw new AttributeValidException(attributeLabel);
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
        return ParamType.NUMBER;
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
        "handler": "formrate",
        "reaction": {
            "hide": {},
            "setvalue": {},
            "disable": {},
            "display": {},
            "emit": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-star",
        "hasValue": true,
        "label": "评分_13",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "isMask": false,
            "showText": false,
            "width": "100%",
            "count": 5,
            "description": "",
            "isDisabled": false,
            "allowHalf": false,
            "isHide": false
        },
        "uuid": "a8fb26d3b69b461a8c992b6a8ec831fa"
    }
     */
    /*
    保存数据结构
    5
     */
    /*
    返回数据结构
    {
        "value": 5
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
    protected void myValidateExtendAttributeConfig(String key, JSONObject config) {
        Integer count = config.getInteger("count");
        if (count == null) {
            throw new FormExtendAttributeConfigIllegalException(this.getHandler(), key, "config.count");
        }
        if (count < 1 || count > 10) {
            throw new FormExtendAttributeConfigIllegalException(this.getHandler(), key, "config.count", count.toString());
        }
    }
}

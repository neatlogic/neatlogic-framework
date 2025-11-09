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

//@Component
public class DivideHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMDIVIDER.getHandler();
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public int getSort() {
        return 21;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return null;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return null;
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return null;
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMDIVIDER.getHandlerName();
    }

    @Override
    public String getIcon() {
        return "ts-minus";
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
        return false;
    }

    @Override
    public boolean isProcessTaskBatchSubmissionTemplateParam() {
        return false;
    }

    @Override
    public boolean isAudit() {
        return false;
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return null;
    }

    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return null;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return null;
    }
}

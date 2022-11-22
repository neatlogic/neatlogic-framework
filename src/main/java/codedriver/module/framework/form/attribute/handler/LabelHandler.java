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
import codedriver.framework.form.exception.AttributeValidException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LabelHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMLABEL.getHandler();
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
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getDataObj();
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getDataObj();
    }

    @Override
    public Object textConversionValue(List<String> values, JSONObject config) {
        if (CollectionUtils.isNotEmpty(values)) {
            return values.get(0);
        }
        return null;
    }

    @Override
    public ParamType getParamType() {
        return ParamType.STRING;
    }


    @Override
    public boolean isConditionable() {
        return false;
    }


    @Override
    public boolean isAudit() {
        return true;
    }

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

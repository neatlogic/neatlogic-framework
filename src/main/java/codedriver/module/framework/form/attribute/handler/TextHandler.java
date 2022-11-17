/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.constvalue.FormHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;

import java.util.List;

@Component
public class TextHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return FormHandler.FORMTEXT.getHandler();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "input";
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
        return true;
    }


    @Override
    public boolean isAudit() {
        return true;
    }

    //表单组件配置信息
//{
//	"handler": "forminput",
//	"label": "文本框_1",
//	"type": "form",
//	"uuid": "f3d875032f0649f7aca5af75d6c37e10",
//	"config": {
//		"isRequired": false,
//		"defaultValueList": "文本a",
//		"ruleList": [],
//		"validList": [],
//		"textType": "none",
//		"quoteUuid": "",
//		"inputMaxlength": 50,
//		"minNumber": "",
//		"maxNumber": "",
//		"decimalNumber": "",
//		"width": "100%",
//		"defaultValueType": "self",
//		"placeholder": "请输入",
//		"authorityConfig": [
//			"common#alluser"
//		]
//	}
//}
    //保存数据结构
//    "文本a"
    //返回数据结构
//{
//	"value": "文本a"
//}
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

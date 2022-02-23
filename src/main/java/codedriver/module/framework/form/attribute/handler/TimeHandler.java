/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.form.constvalue.FormConditionModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;

import java.util.List;

@Component
public class TimeHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return "formtime";
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
    public String getHandlerName() {
        return "时间";
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
    public boolean isAudit() {
        return true;
    }

    //表单组件配置信息
    //{
//	"handler": "formtime",
//	"label": "时间_5",
//	"type": "form",
//	"uuid": "3dd979696e83434daf2ec730fc023fa8",
//	"config": {
//		"isRequired": false,
//		"validType": [],
//		"defaultValueList": "05:05:05",
//		"ruleList": [],
//		"width": "100%",
//		"validValueList": [],
//		"showType": "HH:mm:ss",
//		"validList": [],
//		"quoteUuid": "",
//		"defaultValueType": "self",
//		"placeholder": "请选择时间",
//		"authorityConfig": [
//			"common#alluser"
//		]
//	}
//}
    //保存数据结构
//    "05:05:05"
    //返回数据结构
//{
//	"format": "HH:mm:ss",
//	"value": "05:05:05"
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        resultObj.put("format", configObj.getString("showType"));
        resultObj.put("value", attributeDataVo.getDataObj());
        return resultObj;
    }
}

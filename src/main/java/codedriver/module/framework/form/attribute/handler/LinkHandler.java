/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.form.constvalue.FormConditionModel;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;

import java.util.List;

@Component
public class LinkHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return "formlink";
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getData();
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        String value = configObj.getString("value");
        String text = configObj.getString("text");
        String target = configObj.getString("target");
        return "<a href='" + value + "' target='"+ target +"'>" + text + "</a>";
    }

    @Override
    public Object textConversionValue(List<String> values, JSONObject config) {
        return null;
    }

    @Override
    public String getHandlerName() {
        return "链接";
    }

    @Override
    public String getIcon() {
        return "tsfont-formlink";
    }

    @Override
    public ParamType getParamType() {
        return ParamType.STRING;
    }

    @Override
    public String getDataType() {
        return "string";
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
    public boolean isAudit() {
        return false;
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return null;
    }

    //表单组件配置信息
//{
//	"handler": "formlink",
//	"label": "链接_11",
//	"type": "form",
//	"uuid": "cf9557ac6bac404a8e20355df23f2e11",
//	"config": {
//		"isRequired": false,
//		"ruleList": [],
//		"width": "100%",
//		"validList": [],
//		"quoteUuid": "",
//		"defaultValueType": "self",
//		"text": "百度",
//		"value": "https://www.baidu.com/",
//		"authorityConfig": [
//			"common#alluser"
//		],
//		"target": "_blank"
//	}
//}
    //保存数据结构
//    "https://www.baidu.com/"
    //返回数据结构
//{
//	"text": "百度",
//	"label": "<a href='https://www.baidu.com/' target='_blank'>百度</a>",
//	"value": "https://www.baidu.com/"
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        String value = configObj.getString("value");
        String text = configObj.getString("text");
        String target = configObj.getString("target");
        JSONObject resultObj = new JSONObject();
        resultObj.put("value", value);
        resultObj.put("text", text);
        resultObj.put("label", "<a href='" + value + "' target='"+ target +"'>" + text + "</a>");
        return resultObj;
    }
}

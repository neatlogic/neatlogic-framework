/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.form.attribute.core.IOldAttribute;
import codedriver.framework.form.constvalue.FormHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;

import java.util.List;

@Component
public class EditorHandler extends FormHandlerBase implements IOldAttribute {

    @Override
    public String getHandler() {
        return FormHandler.FORMEDITOR.getHandler();
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
    public int getSort() {
        return 2;
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
        return FormHandler.FORMEDITOR.getHandlerName();
    }

    @Override
    public String getIcon() {
        return "ts-viewmodule";
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

    @Override
    public Boolean isUseFormConfig() {
        return false;
    }

    //表单组件配置信息
//{
//	"handler": "formeditor",
//	"label": "富文本框_3",
//	"type": "form",
//	"uuid": "9b6e8d7342e44127b224af410fc1b5aa",
//	"config": {
//		"isRequired": false,
//		"editorMaxlength": "",
//		"defaultValueList": "文本c",
//		"ruleList": [],
//		"width": "100%",
//		"validList": [],
//		"quoteUuid": "",
//		"defaultValueType": "self",
//		"placeholder": "请输入",
//		"authorityConfig": [
//			"common#alluser"
//		]
//	}
//}
    //保存数据结构
//    "文本c"
    //返回数据结构
//{
//	"value": "文本c"
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

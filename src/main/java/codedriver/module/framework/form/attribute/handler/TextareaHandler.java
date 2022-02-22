/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TextareaHandler extends FormHandlerBase {

    @Override
    public String getHandler() {
        return "formtextarea";
    }

    @Override
    public String getHandlerName() {
        return "文本域";
    }

    @Override
    public String getIcon() {
        return "tsfont-formtextarea";
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
       /* if (model != null && model.equals(ProcessConditionModel.CUSTOM.getValue())) {
            return "input";
        }*/
        return "input";//文本域不管是哪种模式下过滤都是input
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
//	"handler": "formtextarea",
//	"label": "文本域_2",
//	"type": "form",
//	"uuid": "c22797047f5a4306bc23db9983d8a9fc",
//	"config": {
//		"isRequired": false,
//		"defaultValueList": "文本b",
//		"ruleList": [],
//		"width": "100%",
//		"validList": [],
//		"quoteUuid": "",
//		"defaultValueType": "self",
//		"placeholder": "请输入",
//		"authorityConfig": [
//			"common#alluser"
//		],
//		"textareaMaxlength": 500
//	}
//}
    //保存数据结构
//    "文本b"
    //返回数据结构
//{
//	"value": "文本b"
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        resultObj.put("value", attributeDataVo.getDataObj());
        return resultObj;
    }
}

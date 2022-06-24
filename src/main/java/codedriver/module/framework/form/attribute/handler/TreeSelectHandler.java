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
import codedriver.framework.form.treeselect.core.ITreeSelectDataSourceHandler;
import codedriver.framework.form.treeselect.core.TreeSelectDataSourceFactory;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author linbq
 * @since 2021/8/3 18:39
 **/
@Component
public class TreeSelectHandler extends FormHandlerBase {
    @Override
    public String getHandler() {
        return FormHandler.FORMTREESELECT.getHandler();
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMTREESELECT.getHandlerName();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "select";
    }

    @Override
    public String getIcon() {
        return "ts-sitemap";
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
    public boolean isAudit() {
        return true;
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
    public boolean isForTemplate() {
        return true;
    }

    @Override
    public String getModule() {
        return "framework";
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        String data = (String) attributeDataVo.getDataObj();
        if (StringUtils.isNotBlank(data)) {
            String dataSource = configObj.getString("dataSource");
            if (StringUtils.isNotBlank(dataSource)) {
                ITreeSelectDataSourceHandler handler = TreeSelectDataSourceFactory.getHandler(dataSource);
                if (handler != null) {
                    List<String> pathList = handler.valueConversionTextPathList(data);
                    return String.join("/", pathList);
                }
            }
        }
        return data;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return valueConversionText(attributeDataVo, configObj);
    }

    @Override
    public Object textConversionValue(List<String> values, JSONObject config) {
        return null;
    }

    @Override
    public int getSort() {
        return 16;
    }

//表单组件配置信息
//    {
//        "handler": "formtreeselect",
//        "label": "下拉树组件_1",
//        "type": "form",
//        "uuid": "2fd292d9e99940c5add33543a48dd045",
//        "config": {
//            "isRequired": false,
//                    "ruleList": [],
//            "width": "100%",
//                    "validList": [],
//            "quoteUuid": "",
//                    "defaultValueType": "self",
//                    "placeholder": "请选择下拉树",
//                    "value": "",
//                    "authorityConfig": [
//            "common#alluser"
//                    ],
//            "config": {
//                "textName": "name",
//                        "valueName": "uuid",
//                        "url": "/api/rest/knowledge/document/type/tree/forselect"
//            },
//            "dataSource": "knowledgeType",
//                    "url": "/api/rest/knowledge/document/type/tree/forselect"
//        }
//    }
//保存数据
//7174540d09f043948fb4e168045a4094
//返回数据结构
//{
//    "value": "7174540d09f043948fb4e168045a4094",
//    "text": "发布文档",
//    "textList": ["测试", "发布文档"]
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        String value = (String) attributeDataVo.getDataObj();
        resultObj.put("value", value);
        if (StringUtils.isNotBlank(value)) {
            String dataSource = configObj.getString("dataSource");
            if (StringUtils.isNotBlank(dataSource)) {
                ITreeSelectDataSourceHandler handler = TreeSelectDataSourceFactory.getHandler(dataSource);
                if (handler != null) {
                    List<String> pathList = handler.valueConversionTextPathList(value);
                    resultObj.put("textList", pathList);
                    if (CollectionUtils.isNotEmpty(pathList)) {
                        resultObj.put("text", pathList.get(pathList.size() - 1));
                    }
                }
            }
        }
        return resultObj;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return valueConversionText(attributeDataVo, configObj);
    }
}

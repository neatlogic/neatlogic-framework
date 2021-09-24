/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.attribute.core.FormHandlerBase;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.treeselect.core.ITreeSelectDataSourceHandler;
import codedriver.framework.form.treeselect.core.TreeSelectDataSourceFactory;
import com.alibaba.fastjson.JSONObject;
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
        return "formtreeselect";
    }

    @Override
    public String getHandlerName() {
        return "下拉树组件";
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
                    return handler.valueConversionTextPath(data);
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
}

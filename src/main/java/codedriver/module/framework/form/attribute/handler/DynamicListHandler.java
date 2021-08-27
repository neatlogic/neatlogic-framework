/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.restful.core.MyApiComponent;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentFactory;
import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;

import java.util.ArrayList;
import java.util.List;

@Component
public class DynamicListHandler extends FormHandlerBase {

    private final static Logger logger = LoggerFactory.getLogger(DynamicListHandler.class);
    @Override
    public String getHandler() {
        return "formdynamiclist";
    }

    @Override
    public boolean valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return true;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        if (!attributeDataVo.dataIsEmpty()) {
            return "已更新";
        } else {
            return "";
        }
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        String mode = configObj.getString("mode");
        if ("normal".equals(mode)) {
            JSONObject dataObj = (JSONObject) attributeDataVo.getDataObj();
            JSONArray selectUuidList = dataObj.getJSONArray("selectUuidList");
            resultObj.put("selectUuidList", selectUuidList);
            JSONObject table = dataObj.getJSONObject("table");
            if (MapUtils.isNotEmpty(table)) {
                resultObj.put("tbodyList", table.getJSONArray("tbodyList"));
                resultObj.put("theadList", table.getJSONArray("theadList"));
            }
        } else if ("dialog".equals(mode)) {
            JSONArray dataObj = (JSONArray) attributeDataVo.getDataObj();
            if (CollectionUtils.isNotEmpty(dataObj)) {
                String matrixUuid = configObj.getString("matrixUuid");
                String uuidColumn = configObj.getString("uuidColumn");
                List<String> columnList = new ArrayList<>();
                JSONArray columnHeadList = configObj.getJSONArray("dataConfig");
                if (CollectionUtils.isNotEmpty(columnHeadList)) {
                    for (int i = 0; i < columnHeadList.size(); i++) {
                        JSONObject columnHeadObj = columnHeadList.getJSONObject(i);
                        String uuid = columnHeadObj.getString("uuid");
                        if (StringUtils.isNotBlank(uuid)) {
                            columnList.add(uuid);
                        }
                    }
                }
                ApiVo api = PrivateApiComponentFactory.getApiByToken("matrix/column/data/init/fortable");
                if (api != null) {
                    MyApiComponent restComponent = (MyApiComponent) PrivateApiComponentFactory.getInstance(api.getHandler());
                    if (restComponent != null) {
                        JSONObject paramObj = new JSONObject();
                        paramObj.put("matrixUuid", matrixUuid);
                        paramObj.put("columnList", columnList);
                        paramObj.put("uuidList", dataObj);
                        paramObj.put("uuidColumn", uuidColumn);
                        paramObj.put("needPage", false);
                        try {
                            resultObj = (JSONObject) restComponent.myDoService(paramObj);
                            if (MapUtils.isNotEmpty(resultObj)) {
                                resultObj.put("selectUuidList", dataObj);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
        return resultObj;
    }

    @Override
    public Object textConversionValue(List<String> values, JSONObject config) {
        return null;
    }

    @Override
    public String getHandlerName() {
        return "表格选择组件";
    }

    @Override
    public String getIcon() {
        return "tsfont-formdynamiclist";
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
        return false;
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return null;
    }
}

/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.form.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.dto.ExpressionVo;
import neatlogic.framework.form.attribute.core.FormAttributeHandlerFactory;
import neatlogic.framework.form.attribute.core.IFormAttributeHandler;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormAttributeVo implements Serializable {
    private static final long serialVersionUID = 8282018124626035430L;
    @EntityField(name = "属性uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "表单uuid", type = ApiParamType.STRING)
    private String formUuid;
    @EntityField(name = "表单版本uuid", type = ApiParamType.STRING)
    private String formVersionUuid;
    @EntityField(name = "属性标签名", type = ApiParamType.STRING)
    private String label;
    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "处理器", type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "属性配置", type = ApiParamType.STRING)
    private String config;
    @EntityField(name = "属性数据", type = ApiParamType.STRING)
    private String data;
    @EntityField(name = "是否必填", type = ApiParamType.BOOLEAN)
    private boolean isRequired;
    @EntityField(name = "表达式列表", type = ApiParamType.JSONARRAY)
    List<ExpressionVo> expressionList;
    @EntityField(name = "默认表达式", type = ApiParamType.JSONOBJECT)
    ExpressionVo defaultExpression;
    @EntityField(name = "供前端渲染时判断，如果为false则前端页面需使用默认config,true则使用表单管理编辑保存的config", type = ApiParamType.BOOLEAN)
    private boolean isUseFormConfig;
    @EntityField(name = "服务uuid，当表单属性作为工单中心搜索条件时需要使用此属性进行对应", type = ApiParamType.STRING)
    private String channelUuid;
    @EntityField(name = "条件模型")
    private FormConditionModel conditionModel = FormConditionModel.CUSTOM;

    @JSONField(serialize = false)
    private JSONObject configObj;

    @JSONField(serialize = false)
    private Set<String> integrationUuidSet;

    @JSONField(serialize = false)
    private Set<String> matrixUuidSet;

    @JSONField(serialize = false)
    private Map<String, Set<String>> matrixUuidAttributeUuidSetMap;

    public FormAttributeVo() {

    }

    public FormAttributeVo(String formUuid) {
        this.formUuid = formUuid;
    }

    public FormAttributeVo(String formUuid, String formVersionUuid) {
        this.formUuid = formUuid;
        this.formVersionUuid = formVersionUuid;
    }

    public FormAttributeVo(String formUuid, String formVersionUuid, String uuid, String label, String type,
                           String handler, boolean isRequired, String config, String data) {
        this.uuid = uuid;
        this.formUuid = formUuid;
        this.formVersionUuid = formVersionUuid;
        this.label = label;
        this.type = type;
        this.handler = handler;
        this.isRequired = isRequired;
        this.config = config;
        this.data = data;
    }

    public String getChannelUuid() {
        return channelUuid;
    }

    public void setChannelUuid(String channelUuid) {
        this.channelUuid = channelUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getFormVersionUuid() {
        return formVersionUuid;
    }

    public void setFormVersionUuid(String formVersionUuid) {
        this.formVersionUuid = formVersionUuid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public ExpressionVo getDefaultExpression() {
        if (defaultExpression != null) {
            return defaultExpression;
        }
        if (handler == null) {
            return null;
        }
        IFormAttributeHandler formHandler = FormAttributeHandlerFactory.getHandler(handler);
        if (formHandler != null && formHandler.getParamType() != null
                && formHandler.getParamType().getDefaultExpression() != null) {
            defaultExpression = new ExpressionVo(formHandler.getParamType().getDefaultExpression());
        }
        return defaultExpression;
    }

    public void setDefaultExpression(ExpressionVo defaultExpression) {
        this.defaultExpression = defaultExpression;
    }

    public List<ExpressionVo> getExpressionList() {
        if (CollectionUtils.isNotEmpty(expressionList)) {
            return expressionList;
        }
        if (handler == null) {
            return null;
        }
        List<Expression> processExpressionList = null;
        IFormAttributeHandler formHandler = FormAttributeHandlerFactory.getHandler(handler);
        if (formHandler != null && formHandler.getParamType() != null) {
            processExpressionList = formHandler.getParamType().getExpressionList();
        }
        if (CollectionUtils.isEmpty(processExpressionList)) {
            return null;
        }
        expressionList = new ArrayList<>();
        for (Expression processExpression : processExpressionList) {
            expressionList.add(new ExpressionVo(processExpression));
        }
        return expressionList;
    }

    public void setExpressionList(List<ExpressionVo> expressionList) {
        this.expressionList = expressionList;
    }

    public FormConditionModel getConditionModel() {
        return conditionModel;
    }

    public void setConditionModel(FormConditionModel conditionModel) {
        this.conditionModel = conditionModel;
    }

//    public String getHandlerName() {
//        if (handler != null) {
//            IFormAttributeHandler formHandler = FormAttributeHandlerFactory.getHandler(handler);
//            if (formHandler != null) {
//                return formHandler.getHandlerName();
//            }
//        }
//        return null;
//    }

    public String getHandlerType() {
        if (handler == null) {
            return null;
        }

        if (conditionModel == null) {
            return null;
        }
        IFormAttributeHandler formHandler = FormAttributeHandlerFactory.getHandler(handler);
        return formHandler.getHandlerType(conditionModel);
    }

    public Boolean getIsMultiple() {
        if (handler == null) {
            return null;
        }
        if ("formselect".equals(handler)) {
            JSONObject configObj = JSON.parseObject(config);
            return configObj.getBoolean("isMultiple");
        }

        if (conditionModel.equals(FormConditionModel.CUSTOM.getValue())) {
            if ("formcheckbox".equals(handler)) {
                return true;
            } else {
                return false;
            }
        }
        return null;
    }

    public boolean getIsUseFormConfig() {
        return this.isUseFormConfig;
    }

    public void setIsUseFormConfig(boolean isUseFormConfig) {
        this.isUseFormConfig = isUseFormConfig;
    }

    public JSONObject getConfigObj() {
        if (configObj == null && StringUtils.isNotBlank(config)) {
            configObj = JSONObject.parseObject(config);
        }
        return configObj;
    }

    public Set<String> getIntegrationUuidSet() {
        return integrationUuidSet;
    }

    public void setIntegrationUuidSet(Set<String> integrationUuidSet) {
        this.integrationUuidSet = integrationUuidSet;
    }

    public Set<String> getMatrixUuidSet() {
        return matrixUuidSet;
    }

    public void setMatrixUuidSet(Set<String> matrixUuidSet) {
        this.matrixUuidSet = matrixUuidSet;
    }

    public Map<String, Set<String>> getMatrixUuidAttributeUuidSetMap() {
        return matrixUuidAttributeUuidSetMap;
    }

    public void setMatrixUuidAttributeUuidSetMap(Map<String, Set<String>> matrixUuidAttributeUuidSetMap) {
        this.matrixUuidAttributeUuidSetMap = matrixUuidAttributeUuidSetMap;
    }
}

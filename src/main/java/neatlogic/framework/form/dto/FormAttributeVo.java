/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.form.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.dto.ExpressionVo;
import neatlogic.framework.form.attribute.core.FormAttributeHandlerFactory;
import neatlogic.framework.form.attribute.core.IFormAttributeHandler;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.*;

public class FormAttributeVo implements Serializable {
    private static final long serialVersionUID = 8282018124626035430L;
    @EntityField(name = "属性uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "表单uuid", type = ApiParamType.STRING)
    private String formUuid;
    @EntityField(name = "表单版本uuid", type = ApiParamType.STRING)
    private String formVersionUuid;
    @EntityField(name = "父级uuid", type = ApiParamType.STRING)
    private String parentUuid;
    @EntityField(name = "标签", type = ApiParamType.STRING)
    private String tag;
    @EntityField(name = "属性key", type = ApiParamType.STRING)
    private String key;
    @EntityField(name = "属性标签名", type = ApiParamType.STRING)
    private String label;
    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "处理器", type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "属性配置", type = ApiParamType.STRING)
    private JSONObject config;
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
    private Set<String> integrationUuidSet;

    @JSONField(serialize = false)
    private Set<String> matrixUuidSet;

    @JSONField(serialize = false)
    private Map<String, Set<String>> matrixUuidAttributeUuidSetMap;

    private FormAttributeParentVo parent;

    @JSONField(serialize = false)
    private String configStr;

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
                           String handler, boolean isRequired, JSONObject config, String data) {
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

    public JSONObject getConfig() {
        if (config == null && configStr != null) {
            config = JSONObject.parseObject(configStr);
        }
        return config;
    }

    public void setConfig(JSONObject config) {
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
        if (formHandler == null) {
            return null;
        }
        return formHandler.getHandlerType(conditionModel);
    }

    public Boolean getIsMultiple() {
        if (handler == null) {
            return null;
        }
        if ("formselect".equals(handler)) {
            return config.getBoolean("isMultiple");
        }

        if (conditionModel!= null && Objects.equals(conditionModel.getValue(), FormConditionModel.CUSTOM.getValue())) {
            if ("formcheckbox".equals(handler)) {
                return true;
            } else {
                return false;
            }
        }
        return null;
    }

    public Boolean getIsUseFormConfig() {
        return this.isUseFormConfig;
    }

    public void setIsUseFormConfig(Boolean isUseFormConfig) {
        this.isUseFormConfig = isUseFormConfig;
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

    public FormAttributeParentVo getParent() {
        return parent;
    }

    public void setParent(FormAttributeParentVo parent) {
        this.parent = parent;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getConfigStr() {
        if (configStr == null && config != null) {
            configStr = config.toJSONString();
        }
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }
}

package neatlogic.framework.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.form.constvalue.FormHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConditionParamVo implements Serializable, Cloneable {
    private String uuid;
    private String name;
    private String label;
    private String controller;
    private JSONObject config;
    private String type;
    private String paramType;
    private String paramTypeName;
    private String defaultExpression;
    private List<ExpressionVo> expressionList = new ArrayList<>();
    private int isEditable = 1;
    private String freemarkerTemplate;
    private String handler;
    private String handlerName;

    private boolean isConditionable;
    List<ConditionParamVo> subParamList = new ArrayList<>();

    /**
     * 不作为数据库与显示字段，仅为排序使用
     */
    @JSONField(serialize = false)
    private Long lcd = 0L;

    public ConditionParamVo() {
    }

    public ConditionParamVo(ConditionParamVo conditionParamVo) {
        this.name = conditionParamVo.name;
        this.label = conditionParamVo.label;
        this.controller = conditionParamVo.controller;
        this.setConfig(conditionParamVo.getConfigStr());
        this.type = conditionParamVo.type;
        this.paramType = conditionParamVo.paramType;
        this.paramTypeName = conditionParamVo.paramTypeName;
        this.defaultExpression = conditionParamVo.defaultExpression;
        this.isEditable = conditionParamVo.isEditable;
        this.freemarkerTemplate = conditionParamVo.freemarkerTemplate;
        this.handler = conditionParamVo.handler;
        this.subParamList = conditionParamVo.subParamList;

        if (CollectionUtils.isNotEmpty(conditionParamVo.expressionList)) {
            for (ExpressionVo expressionVo : conditionParamVo.expressionList) {
                this.expressionList.add(new ExpressionVo(expressionVo));
            }
        }
    }

    public String getHandlerName() {
        if (StringUtils.isNotBlank(handler)) {
            handlerName = FormHandler.getName(handler);
        }
        return handlerName;
    }


    public List<ConditionParamVo> getSubParamList() {
        return subParamList;
    }

    public void setSubParamList(List<ConditionParamVo> subParamList) {
        this.subParamList = subParamList;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(String config) {
        try {
            this.config = JSON.parseObject(config);
            if ("formdate".equals(this.handler)) {
                if (MapUtils.isNotEmpty(this.config)) {
                    this.config.put("type", "datetimerange");
                }
            } else if ("formtime".equals(this.handler)) {
                if (MapUtils.isNotEmpty(this.config)) {
                    this.config.put("type", "timerange");
                }
            }
            //分析config数据，如果有dataConfig配置，则直接获取下层数据
            JSONArray dataConfig = this.config.getJSONArray("dataConfig");
            if (CollectionUtils.isNotEmpty(dataConfig)) {
                for (int i = 0; i < dataConfig.size(); i++) {
                    JSONObject dataObj = dataConfig.getJSONObject(i);
                    ConditionParamVo conditionParamVo = new ConditionParamVo();
                    conditionParamVo.setName(dataObj.getString("uuid"));
                    conditionParamVo.setLabel(dataObj.getString("label"));
                    //conditionParamVo.setController(formAttributeVo.getHandlerType());
                    //conditionParamVo.setType(formAttributeVo.getType());
                    conditionParamVo.setHandler(dataObj.getString("handler"));
                    conditionParamVo.setConfig(dataObj.getJSONObject("config"));
                    if (this.subParamList == null) {
                        this.subParamList = new ArrayList<>();
                    }
                    this.subParamList.add(conditionParamVo);
                }
            }

        } catch (Exception ignored) {

        }
    }

    @JSONField(serialize = false)
    public String getConfigStr() {
        if (config != null) {
            return config.toJSONString();
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultExpression() {
        return defaultExpression;
    }

    public void setDefaultExpression(String defaultExpression) {
        this.defaultExpression = defaultExpression;
    }

    public List<ExpressionVo> getExpressionList() {
        return expressionList;
    }

    public void setExpressionList(List<ExpressionVo> expressionList) {
        this.expressionList = expressionList;
    }

    public int getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(int isEditable) {
        this.isEditable = isEditable;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamTypeName() {
        return paramTypeName;
    }

    public void setParamTypeName(String paramTypeName) {
        this.paramTypeName = paramTypeName;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getFreemarkerTemplate() {
        if (StringUtils.isBlank(freemarkerTemplate) && StringUtils.isNotBlank(paramType)
                && StringUtils.isNotBlank(name)) {
            ParamType paramTypeEnum = ParamType.getParamType(paramType);
            if (paramTypeEnum != null) {
                freemarkerTemplate = paramTypeEnum.getFreemarkerTemplate(name);
            }
        }
        return freemarkerTemplate;
    }

    public void setFreemarkerTemplate(String freemarkerTemplate) {
        this.freemarkerTemplate = freemarkerTemplate;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public boolean isConditionable() {
        return isConditionable;
    }

    public void setConditionable(boolean conditionable) {
        isConditionable = conditionable;
    }

    public Long getLcd() {
        return lcd;
    }

    public void setLcd(Long lcd) {
        this.lcd = lcd;
    }

    @Override
    public ConditionParamVo clone() throws CloneNotSupportedException {
        return (ConditionParamVo) super.clone();
    }
}

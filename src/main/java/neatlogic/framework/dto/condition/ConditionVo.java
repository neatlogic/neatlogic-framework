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

package neatlogic.framework.dto.condition;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.ConditionParamContext;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.condition.core.ConditionHandlerFactory;
import neatlogic.framework.condition.core.IConditionHandler;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.ConditionUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConditionVo implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ConditionVo.class);
    private static final long serialVersionUID = -776692828809703841L;

    private String uuid;
    private String name;
    private String type;
    private String handler;
    private String expression;
    private Integer isShowConditionValue;
    private Object valueList;
    private Boolean result;
    private String error;
    @EntityField(name = "条件名", type = ApiParamType.STRING)
    private String label;
    @EntityField(name = "条件表达式列表", type = ApiParamType.JSONARRAY)
    private List<Expression> expressionList;
    @EntityField(name = "条件值名", type = ApiParamType.STRING)
    private Object text;


    public ConditionVo() {
        super();
    }

    public ConditionVo(JSONObject jsonObj) {
        this.uuid = jsonObj.getString("uuid");
        this.name = jsonObj.getString("name");
        this.type = jsonObj.getString("type");
        this.handler = jsonObj.getString("handler");
        this.expression = jsonObj.getString("expression");
        this.isShowConditionValue = jsonObj.getInteger("isShowConditionValue");
        String values = jsonObj.getString("valueList");
        if (StringUtils.isNotBlank(values)) {
            if (values.startsWith("[") && values.endsWith("]")) {
                this.valueList = JSON.parseArray(values);
            } else {
                this.valueList = values;
            }
        }
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

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Integer getIsShowConditionValue() {
        return isShowConditionValue;
    }

    public void setIsShowConditionValue(Integer isShowConditionValue) {
        this.isShowConditionValue = isShowConditionValue;
    }

    public Object getValueList() {
        return valueList;
    }

    public void setValueList(Object valueList) {
        this.valueList = valueList;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean predicate() {
        result = false;
        ConditionParamContext context = ConditionParamContext.get();
        if (context != null) {
            List<String> currentValueList = new ArrayList<>();
            JSONObject paramData = context.getParamData();
            Object paramValue = paramData.get(this.name);
            if (paramValue != null) {
                if (paramValue instanceof String) {
                    currentValueList.add(GroupSearch.removePrefix((String) paramValue));
                } else if (paramValue instanceof List) {
                    List<String> values = JSON.parseArray(JSON.toJSONString(paramValue), String.class);
                    for (String value : values) {
                        currentValueList.add(GroupSearch.removePrefix(value));
                    }
                } else {
                    currentValueList.add(GroupSearch.removePrefix(paramValue.toString()));
                }
            }

            List<String> targetValueList = new ArrayList<>();
            if (valueList != null) {
                if (valueList instanceof String) {
                    targetValueList.add(GroupSearch.removePrefix((String) valueList));
                } else if (valueList instanceof List) {
                    List<String> values = JSON.parseArray(JSON.toJSONString(valueList), String.class);
                    for (String value : values) {
                        targetValueList.add(GroupSearch.removePrefix(value));
                    }
                } else {
                    targetValueList.add(GroupSearch.removePrefix(valueList.toString()));
                }
            }
            try {
                result = ConditionUtil.predicate(currentValueList, this.expression, targetValueList);
            } catch (Exception e) {
                error = e.getMessage();
                logger.warn(e.getMessage(), e);
            }
            /* 将参数名称、表达式、值的value翻译成对应text，目前条件步骤生成活动时用到**/
            if (context.isTranslate()) {
                if ("common".equals(type)) {
                    IConditionHandler conditionHandler = ConditionHandlerFactory.getHandler(name);
                    if (conditionHandler != null) {
                        valueList = conditionHandler.valueConversionText(valueList, null);
                        name = conditionHandler.getDisplayName();
                    }
                } else if ("form".equals(type)) {
                    IConditionHandler conditionHandler = ConditionHandlerFactory.getHandler("form");
                    if (conditionHandler != null) {
                        JSONObject formConfig = context.getFormConfig();
                        if (MapUtils.isNotEmpty(formConfig)) {
                            JSONObject configObj = new JSONObject();
                            configObj.put("attributeUuid", name);
                            configObj.put("formConfig", formConfig);
                            valueList = conditionHandler.valueConversionText(valueList, configObj);
                            name = configObj.getString("name");
                        }
                    }
                }
                this.expression = Expression.getExpressionName(this.expression);
            }
        }

        return result;
    }

    public Boolean getResult() {
        return result;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Expression> getExpressionList() {
        return expressionList;
    }

    public void setExpressionList(List<Expression> expressionList) {
        this.expressionList = expressionList;
    }

    public Object getText() {
        return text;
    }

    public void setText(Object text) {
        this.text = text;
    }
}

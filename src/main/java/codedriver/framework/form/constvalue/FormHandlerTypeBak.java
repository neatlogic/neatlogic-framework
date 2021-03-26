/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.constvalue;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.common.constvalue.FormHandlerType;
import codedriver.framework.common.constvalue.ParamType;

public enum FormHandlerTypeBak implements IFormHandlerType {

    FORMSELECT("formselect", "下拉框", "ts-sitemap", "form", FormHandlerType.SELECT, ParamType.ARRAY,
            List.class.getSimpleName().toLowerCase(), true, true, true, true, false),
    FORMINPUT("forminput", "文本框", "ts-textmodule", "form", FormHandlerType.INPUT, ParamType.STRING,
            String.class.getSimpleName().toLowerCase(), true, true, true, false, false),
    FORMTEXTAREA("formtextarea", "文本域", "ts-text", "form", FormHandlerType.TEXTAREA, ParamType.STRING,
            String.class.getSimpleName().toLowerCase(), true, true, true, false, false),
    FORMEDITOR("formeditor", "富文本框", "ts-viewmodule", "form", FormHandlerType.EDITOR, ParamType.STRING,
            String.class.getSimpleName().toLowerCase(), Arrays.asList(Expression.LIKE, Expression.NOTLIKE), Expression.LIKE,
            true, true, true, false, false),
    FORMRADIO("formradio", "单选框", "ts-complete", "form", FormHandlerType.RADIO, ParamType.ARRAY,
            String.class.getSimpleName().toLowerCase(), true, true, true, true, false),
    FORMCHECKBOX("formcheckbox", "复选框", "ts-check-square-o", "form", FormHandlerType.CHECKBOX, ParamType.ARRAY,
            List.class.getSimpleName().toLowerCase(), true, true, true, true, false),
    FORMDATE("formdate", "日期", "ts-calendar", "form", FormHandlerType.DATE, ParamType.DATE,
            String.class.getSimpleName().toLowerCase(), true, true, true, false, false),
    FORMTIME("formtime", "时间", "ts-timer", "form", FormHandlerType.TIME, ParamType.DATE, "string", true, true, true,
            false, false),
    FORMSTATICLIST("formstaticlist", "静态列表", "ts-list", "form", false, true, true, false, false),
    FORMCASCADELIST("formcascadelist", "级联下拉", "ts-formlist", "form", FormHandlerType.CASCADELIST, ParamType.STRING,
            "string", true, true, true, false, false),
    FORMDYNAMICLIST("formdynamiclist", "动态列表", "ts-viewlist", "form", false, true, false, false, false),
    FORMDIVIDER("formdivider", "分割线", "ts-minus", "form", false, false, false, false, false),
    FORMUSERSELECT("formuserselect", "用户选择器", "ts-user", "form", FormHandlerType.USERSELECT, ParamType.ARRAY, "string",
            true, true, true, false, false),
    FORMLINK("formlink", "链接", "ts-link", "form", null, ParamType.STRING, "string", false, true, false, false, false),
    FORMPRIORITY("formpriority", "修改优先级", "ts-user", "control", false, false, false, true, false);

    private String handler;
    private String handlerName;
    private FormHandlerType handlerType;
    private ParamType paramType;
    private String dataType;
    private Expression expression;
    private List<Expression> expressionList;
    private String icon;
    private String type;
    private boolean isConditionable = true;// 是否可设置为条件
    private boolean isShowable = true;// 是否可设置显示隐藏
    private boolean isValueable = true;// 是否可设置赋值
    private boolean isFilterable = true;// 是否可设置过滤
    private boolean isExtendable = false;// 是否有拓展属性

    private String module = "process";

    public boolean isConditionable() {
        return isConditionable;
    }

    public boolean isShowable() {
        return isShowable;
    }

    public boolean isValueable() {
        return isValueable;
    }

    public boolean isFilterable() {
        return isFilterable;
    }

    public boolean isExtendable() {
        return isExtendable;
    }

    public String getModule() {
        return module;
    }

    private FormHandlerTypeBak(String _handler, String _handlerName, String _icon, String _type,
                               boolean _isConditionable, boolean _isShowable, boolean _isValueable, boolean _isFilterable,
                               boolean _isExtendable) {
        this(_handler, _handlerName, _icon, _type, null, null, null, _isConditionable, _isShowable, _isValueable,
                _isFilterable, _isExtendable);
    }

    private FormHandlerTypeBak(String _handler, String _handlerName, String _icon, String _type,
                               FormHandlerType _handlerType, ParamType _paramType, String _dataType, boolean _isConditionable,
                               boolean _isShowable, boolean _isValueable, boolean _isFilterable, boolean _isExtendable) {
        this(_handler, _handlerName, _icon, _type, _handlerType, _paramType, _dataType, null, null, _isConditionable,
                _isShowable, _isValueable, _isFilterable, _isExtendable);
    }

    private FormHandlerTypeBak(String _handler, String _handlerName, String _icon, String _type,
                               FormHandlerType _handlerType, ParamType _paramType, String _dataType, List<Expression> _expressionList,
                               Expression _expression, boolean _isConditionable, boolean _isShowable, boolean _isValueable,
                               boolean _isFilterable, boolean _isExtendable) {
        this.handler = _handler;
        this.handlerName = _handlerName;
        this.handlerType = _handlerType;
        this.paramType = _paramType;
        this.dataType = _dataType;
        this.expressionList = _expressionList;
        this.expression = _expression;
        this.icon = _icon;
        this.type = _type;
        this.isConditionable = _isConditionable;
        this.isShowable = _isShowable;
        this.isValueable = _isValueable;
        this.isFilterable = _isFilterable;
        this.isExtendable = _isExtendable;
    }

    public static String getHandlerName(String _handler) {
        for (FormHandlerTypeBak s : values()) {
            if (s.getHandler().equals(_handler)) {
                return s.getHandlerName();
            }
        }
        return null;
    }

    public static FormHandlerType getHandlerType(String _handler, FormConditionModel processWorkcenterConditionType) {
        for (FormHandlerTypeBak s : values()) {
            if (s.getHandler().equals(_handler)) {
                return s.getHandlerType(processWorkcenterConditionType);
            }
        }
        return null;
    }

    public static List<Expression> getExpressionList(String _handler) {
        for (FormHandlerTypeBak s : values()) {
            if (s.getHandler().equals(_handler)) {
                return s.getExpressionList();
            }
        }
        return null;
    }

    public FormHandlerType getHandlerType(FormConditionModel processWorkcenterConditionType) {
        if (FormConditionModel.CUSTOM == processWorkcenterConditionType) {
            if (handlerType == FormHandlerType.RADIO || handlerType == FormHandlerType.CHECKBOX) {
                return FormHandlerType.SELECT;
            }
            if (handlerType == FormHandlerType.TEXTAREA || handlerType == FormHandlerType.EDITOR) {
                return FormHandlerType.INPUT;
            }
        }
        return handlerType;
    }

    public static Expression getExpression(String _handler) {
        for (FormHandlerTypeBak s : values()) {
            if (s.getHandler().equals(_handler)) {
                return s.getExpression();
            }
        }
        return null;
    }

    public static String getDataType(String _handler) {
        for (FormHandlerTypeBak s : values()) {
            if (s.getHandler().equals(_handler)) {
                return s.getDataType();
            }
        }
        return null;
    }

    public List<Expression> getExpressionList() {
        if (CollectionUtils.isEmpty(expressionList) && paramType != null) {
            expressionList = this.paramType.getExpressionList();
        }
        return expressionList;
    }

    public String getHandler() {
        return handler;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public String getDataType() {
        return dataType;
    }

    public Expression getExpression() {
        if (expression == null && paramType != null) {
            expression = this.paramType.getDefaultExpression();
        }
        return expression;
    }

    public ParamType getParamType() {
        return paramType;
    }

    public static ParamType getParamType(String _handler) {
        for (FormHandlerTypeBak s : values()) {
            if (s.getHandler().equals(_handler)) {
                return s.getParamType();
            }
        }
        return null;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public FormHandlerType getHandlerType() {
        return handlerType;
    }
}

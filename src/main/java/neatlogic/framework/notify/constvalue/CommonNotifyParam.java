/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.notify.constvalue;

import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.notify.core.INotifyParam;

/**
 * @author linbq
 * @since 2021/10/18 11:57
 **/
public enum CommonNotifyParam implements INotifyParam {
    OPERATOR("operator", "操作人", ParamType.STRING),
    HOMEURL("homeUrl", "域名", ParamType.STRING, "<a href=\"${homeUrl}\" target=\"_blank\"></a>"),
    ;

    private final String value;
    private final String text;
    private final ParamType paramType;
    private String freemarkerTemplate;

    CommonNotifyParam(String value, String text, ParamType paramType) {
        this(value, text, paramType, null);
    }

    CommonNotifyParam(String value, String text, ParamType paramType, String freemarkerTemplate) {
        this.value = value;
        this.text = text;
        this.paramType = paramType;
        this.freemarkerTemplate = freemarkerTemplate;
    }
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public ParamType getParamType() {
        return paramType;
    }

    @Override
    public String getFreemarkerTemplate() {
        return freemarkerTemplate;
    }
}

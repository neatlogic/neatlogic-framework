/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.notify.exception;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.notify.core.INotifyParam;

/**
 * @author laiwt
 * @since 2021/10/21 13:49
 **/
public enum ExceptionNotifyParam implements INotifyParam {

    EXCEPTIONSTACK("exceptionstack", "异常信息", ParamType.STRING);

    private final String value;
    private final String text;
    private final ParamType paramType;

    ExceptionNotifyParam(String value, String text, ParamType paramType) {
        this.value = value;
        this.text = text;
        this.paramType = paramType;
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
}

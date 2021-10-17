/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.core;

import codedriver.framework.common.constvalue.ParamType;

/**
 * @author linbq
 * @since 2021/10/15 17:11
 **/
public interface INotifyParam {
    String getValue();
    String getText();
    ParamType getParamType();
    default String getFreemarkerTemplate(){
        if (getParamType() != null && getValue() != null) {
            return getParamType().getFreemarkerTemplate(getValue());
        }
        return null;
    }
}

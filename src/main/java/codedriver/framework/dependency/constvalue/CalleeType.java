/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.constvalue;

import codedriver.framework.dependency.core.ICalleeType;

/**
 * @author: linbq
 * @since: 2021/4/2 10:30
 **/
public enum CalleeType implements ICalleeType {
    MATRIX("matrix", "矩阵"),
    FORM("form", "表单"),
    INTEGRATION("integration", "集成"),
    WORKTIME("worktime", "服务窗口"),
    NOTIFYPOLICY("notifypolicy", "通知策略");

    private String value;
    private String text;

    private CalleeType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getText() {
        return text;
    }
}

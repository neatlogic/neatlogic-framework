/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.constvalue;

import codedriver.framework.dependency.core.ICalleeType;

/**
 * 被调用者类型
 *
 * @author: linbq
 * @since: 2021/4/2 10:30
 **/
public enum CalleeType implements ICalleeType {
    MATRIX("matrix", "矩阵"),
    FORM("form", "表单"),
    INTEGRATION("integration", "集成"),
    WORKTIME("worktime", "服务窗口"),
    NOTIFY_POLICY("notifypolicy", "通知策略");

    private String value;
    private String text;

    private CalleeType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 被调用者类型值
     *
     * @return
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * 被调用者类型名
     *
     * @return
     */
    @Override
    public String getText() {
        return text;
    }
}
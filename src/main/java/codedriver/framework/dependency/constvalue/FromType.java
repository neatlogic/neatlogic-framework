/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.constvalue;

import codedriver.framework.dependency.core.IFromType;

/**
 * 被调用者类型
 *
 * @author: linbq
 * @since: 2021/4/2 10:30
 **/
public enum FromType implements IFromType {
    MATRIX("matrix", "矩阵"),
    MATRIXATTR("matrixattr", "矩阵属性"),
    FORM("form", "表单"),
    FORMATTR("formattr", "表单属性"),
    INTEGRATION("integration", "集成"),
    CMDBCI("cmdbci", "cmdb模型"),
    CMDBCIATTR("cmdbciattr", "cmdb模型属性"),
    WORKTIME("worktime", "服务窗口"),
    NOTIFY_POLICY("notifypolicy", "通知策略");

    private String value;
    private String text;

    private FromType(String value, String text) {
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

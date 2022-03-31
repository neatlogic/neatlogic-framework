/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dependency.constvalue;

import codedriver.framework.dependency.core.IFromType;

/**
 * 被引用者（上游）类型
 *
 * @author: linbq
 * @since: 2021/4/2 10:30
 **/
public enum FrameworkFromType implements IFromType {
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

    FrameworkFromType(String value, String text) {
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
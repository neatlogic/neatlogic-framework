/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.restful.enums;

public enum TreeMenuType {
    SYSTEM("system", "系统接口目录"), CUSTOM("custom", "自定义接口目录"), AUDIT("audit", "操作审计目录");

    private final String name;
    private final String text;

    TreeMenuType(String _name, String _text) {
        this.name = _name;
        this.text = _text;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return text;
    }

    public static String getText(String name) {
        for (TreeMenuType s : TreeMenuType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}

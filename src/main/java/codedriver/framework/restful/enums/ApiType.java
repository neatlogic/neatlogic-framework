/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.enums;

public enum ApiType {
    OBJECT("object", "对象模式", "rest/"), STREAM("stream", "json流模式", "stream/"), BINARY("binary", "字节流模式", "binary/");

    private final String name;
    private final String text;
    private final String urlPre;

    ApiType(String _name, String _text, String _urlPre) {
        this.name = _name;
        this.text = _text;
        this.urlPre = _urlPre;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getUrlPre() {
        return urlPre;
    }

    public static String getText(String name) {
        for (ApiType s : ApiType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }

    public static String getUrlPre(String name) {
        for (ApiType s : ApiType.values()) {
            if (s.getValue().equals(name)) {
                return s.getUrlPre();
            }
        }
        return "";
    }
}

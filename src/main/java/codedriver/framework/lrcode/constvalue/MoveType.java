package codedriver.framework.lrcode.constvalue;

/**
 * @Title: MoveType
 * @Package codedriver.framework.lrcode.constvalue
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/3/17 17:24
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public enum MoveType {
    INNER("inner", "移动到目标节点里面"),
    PREV("prev", "移动到目标节点前面"),
    NEXT("next", "移动到目标节点后面")
    ;
    private String value;
    private String text;

    MoveType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

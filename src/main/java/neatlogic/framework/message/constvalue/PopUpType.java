package neatlogic.framework.message.constvalue;

/**
 * @Title: PopUpType
 * @Package neatlogic.framework.message.constvalue
 * @Description: 消息桌面推送方式枚举
 * @Author: linbq
 * @Date: 2020/12/31 10:51
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public enum PopUpType {
    SHORTSHOW("shortshow", "临时"),
    LONGSHOW("longshow", "持续"),
    CLOSE("close", "关闭")
    ;

    private String value;
    private String text;

    PopUpType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}

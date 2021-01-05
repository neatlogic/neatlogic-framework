package codedriver.framework.message.constvalue;

/**
 * @Title: PopUpType
 * @Package codedriver.framework.news.constvalue
 * @Description: 消息桌面推送方式枚举
 * @Author: linbq
 * @Date: 2020/12/31 10:51
 **/
public enum PopUpType {
    SHORTSHOW("shortshow", "临时"),
    longshow("longshow", "持续"),
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

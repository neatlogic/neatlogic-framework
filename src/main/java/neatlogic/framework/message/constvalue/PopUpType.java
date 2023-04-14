package neatlogic.framework.message.constvalue;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

/**
 * @Title: PopUpType
 * @Package neatlogic.framework.message.constvalue
 * @Description: 消息桌面推送方式枚举
 * @Author: linbq
 * @Date: 2020/12/31 10:51
 **/
public enum PopUpType {
    SHORTSHOW("shortshow", new I18n("enum.framework.popuptype.shortshow")),
    LONGSHOW("longshow", new I18n("enum.framework.popuptype.longshow")),
    CLOSE("close", new I18n("enum.framework.popuptype.close"));

    private String value;
    private I18n text;

    PopUpType(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }
}

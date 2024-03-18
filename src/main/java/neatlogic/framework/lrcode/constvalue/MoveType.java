package neatlogic.framework.lrcode.constvalue;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

/**
 * @Title: MoveType
 * @Package neatlogic.framework.lrcode.constvalue
 * @Description: 移动类型
 * @Author: linbq
 * @Date: 2021/3/17 17:24
 **/
public enum MoveType {
    INNER("inner", new I18n("移动到目标节点里面")),
    PREV("prev", new I18n("移动到目标节点前面")),
    NEXT("next", new I18n("移动到目标节点后面"));
    private String value;
    private I18n text;

    MoveType(String value, I18n text) {
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
        return $.t(text.toString());
    }

    public void setText(String text) {
        this.text = new I18n(text);
    }

    public static MoveType getMoveType(String _value) {
        for (MoveType e : values()) {
            if (e.getValue().equals(_value)) {
                return e;
            }
        }
        return null;
    }
}

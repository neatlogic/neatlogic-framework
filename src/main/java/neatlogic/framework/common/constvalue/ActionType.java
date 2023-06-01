package neatlogic.framework.common.constvalue;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

/**
 *
* @Author:linbq
* @Time:2020年8月18日
* @ClassName: ActionType
* @Description: 操作类型枚举类
 */
public enum ActionType {
    CREATE("create", new I18n("创建")),
    UPDATE("update", new I18n("修改"));
    private final String value;
    private final I18n text;
    private ActionType(String value, I18n text) {
        this.value = value;
        this.text = text;
    }
    public String getValue() {
        return value;
    }
    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }
    public static String getText(String value) {
        for(ActionType type : values()) {
            if(type.getValue().equals(value)) {
                return type.getText();
            }
        }
        return "";
    }
}

package neatlogic.framework.common.constvalue;
/**
 * 
* @Author:linbq
* @Time:2020年8月18日
* @ClassName: ActionType 
* @Description: 操作类型枚举类
 */
public enum ActionType {
    CREATE("create", "创建"),
    UPDATE("update", "修改");
    private String value;
    private String text;
    private ActionType(String value, String text) {
        this.value = value;
        this.text = text;
    }
    public String getValue() {
        return value;
    }
    public String getText() {
        return text;
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

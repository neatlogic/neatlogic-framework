package codedriver.framework.reminder.core;

public enum OperationTypeEnum {
    CREATE("create","增加"),
    DELETE("delete","删除"),
    UPDATE("update","更新"),
    SEARCH("search","查询");
    private String name;
    private String text;

    private OperationTypeEnum(String _value, String _text){
        this.name = _value;
        this.text = _text;
    }

    public String getValue(){
        return name;
    }

    public String getText(){
        return text;
    }

    public static String getText(String value){
        for (OperationTypeEnum f : OperationTypeEnum.values()){
            if (f.getValue().equals(value)){
                return f.getText();
            }
        }
        return "";
    }
}

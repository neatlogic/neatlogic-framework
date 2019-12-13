package codedriver.framework.reminder.core;

public enum ControlEnum {
    RADIO("radio", "单选"), SELECT("select", "下拉单选框"), MSELECT("mselect", "多选下拉框");
    private String name;
    private String text;

    private ControlEnum(String _value, String _text){
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
        for (ControlEnum f : ControlEnum.values()){
            if (f.getValue().equals(value)){
                return f.getText();
            }
        }
        return "";
    }
}

package codedriver.framework.auth.core;

public enum AuthGroupEnum {

    FRAMEWORK("基础组", "framework"),
    PROCESS("流程管理组", "process");

    private String text;
    private String value;

    private AuthGroupEnum(String _text, String _value){
        this.text = _text;
        this.value = _value;
    }

    public String getValue(){
        return value;
    }

    public String getText(){
        return text;
    }

    public static String getText(String value){
        for (AuthGroupEnum f : AuthGroupEnum.values()){
            if (f.getValue().equals(value)){
                return f.getText();
            }
        }
        return "";
    }
}

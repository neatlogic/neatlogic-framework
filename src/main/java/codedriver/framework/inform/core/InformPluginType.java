package codedriver.framework.inform.core;

public enum InformPluginType {
    EMAIL("emailPlugin", "emailPlugin");

    private String name;
    private String text;

    InformPluginType(String _name, String _text){
        this.name = _name;
        this.text = _text;
    }

    public String getValue(){
        return name;
    }

    public String getText(){
        return text;
    }

    public static String getText(String name){
        for (InformPluginType s : InformPluginType.values()){
            if (name != null && name.equals(s.getValue())){
                return s.getText();
            }
        }
        return "";
    }

    public static InformPluginType getInformParamType(String name){
        for (InformPluginType s : InformPluginType.values()){
            if (name != null && name.equals(s.getValue())){
                return s;
            }
        }
        return null;
    }
}

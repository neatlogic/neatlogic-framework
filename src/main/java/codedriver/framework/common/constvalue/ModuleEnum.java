package codedriver.framework.common.constvalue;

import java.util.Arrays;
import java.util.List;

public enum ModuleEnum implements IModuleEnum{

    FRAMEWORK("系统管理", "framework",Arrays.asList("framework", "globalsearch"));
   

    private String text;
    private String value;
    private List<String> moduleList;

    private ModuleEnum(String _text, String _value ,List<String> _moduleList){
        this.text = _text;
        this.value = _value;
        this.moduleList = _moduleList;
    }

    @Override
    public String getValue(){
        return value;
    }
    
    @Override
    public String getText(){
        return text;
    }
    
    @Override
    public List<String> getModuleList(){
        return moduleList;
    }
}

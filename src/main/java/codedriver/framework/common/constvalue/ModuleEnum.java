package codedriver.framework.common.constvalue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dto.ModuleVo;

public enum ModuleEnum {

	PROCESS("ITSM", "process","process"),
    FRAMEWORK("系统管理", "framework","framework");
   

    private String text;
    private String value;
    private String module;

    private ModuleEnum(String _text, String _value ,String _module){
        this.text = _text;
        this.value = _value;
        this.module = _module;
    }

    public String getValue(){
        return value;
    }

    public String getText(){
        return text;
    }
    
    public String getModule(){
        return module;
    }

    public static String getText(String value){
        for (ModuleEnum f : ModuleEnum.values()){
            if (f.getValue().equals(value)){
                return f.getText();
            }
        }
        return "";
    }
    
    public static Map<String ,String> getActiveModule(){
    	Map<String ,String> activeModuleMap = new HashMap<String,String>();
    	List<ModuleVo> activeModuleList = TenantContext.get().getActiveModuleList();
        for (ModuleEnum f : ModuleEnum.values()){
        	List<ModuleVo> list = activeModuleList.stream().filter(o ->o.getId().equals(f.getModule())).collect(Collectors.toList());
            if (list.size()>0){
            	activeModuleMap.put(f.getValue(), f.getText());
            }
        }
        return activeModuleMap;
    }
}

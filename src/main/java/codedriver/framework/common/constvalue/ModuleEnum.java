package codedriver.framework.common.constvalue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dto.ModuleVo;

public enum ModuleEnum {

	PROCESS("ITSM", "process", Arrays.asList("process")),
    FRAMEWORK("系统管理", "framework",Arrays.asList("framework", "globalsearch"));
   

    private String text;
    private String value;
    private List<String> moduleList;

    private ModuleEnum(String _text, String _value ,List<String> _moduleList){
        this.text = _text;
        this.value = _value;
        this.moduleList = _moduleList;
    }

    public String getValue(){
        return value;
    }

    public String getText(){
        return text;
    }
    
    public List<String> getModuleList(){
        return moduleList;
    }

    public static String getText(String value){
        for (ModuleEnum f : ModuleEnum.values()){
            if (f.getValue().equals(value)){
                return f.getText();
            }
        }
        return "";
    }
    
    public static List<String> getModuleList(String value){
    	for (ModuleEnum f : ModuleEnum.values()){
            if (f.getValue().equals(value)){
                return f.getModuleList();
            }
        }
    	return new ArrayList<String>();
    }
    public static Map<String ,String> getActiveModule(){
    	Map<String ,String> activeModuleMap = new HashMap<String,String>();
    	List<ModuleVo> activeModuleList = TenantContext.get().getActiveModuleList();
        for (ModuleEnum f : ModuleEnum.values()){
        	List<ModuleVo> list = activeModuleList.stream().filter(o ->f.getModuleList().contains(o.getId())).collect(Collectors.toList());
            if (list.size()>0){
            	activeModuleMap.put(f.getValue(), f.getText());
            }
        }
        return activeModuleMap;
    }
}

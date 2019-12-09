package codedriver.framework.inform.core;

import codedriver.framework.inform.dto.InformVo;
import codedriver.framework.inform.dto.MessageVo;

public interface InformComponentBase {
    /** 
    * @Description: 处理通知 
    * @Param: [informVo] 
    * @return: void  
    */ 
    void execute(MessageVo messageVo);
    
    /** 
    * @Description: 插件ID 
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    String getId();

    String getMduleId();

    String getTemplateContent();

    String getTempateTitle();
}

package codedriver.framework.reminder.core;

import codedriver.framework.reminder.dto.GlobalReminderMessageVo;
import codedriver.framework.reminder.dto.ReminderMessageVo;
import codedriver.framework.reminder.dto.param.GlobalReminderHandlerParamVo;

import java.util.List;

/**
* @Description: 系统通知
*/ 
public interface IGlobalReminderHandler {
    /** 
    * @Description: 名称 
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    String getName();

    /**
    * @Description: 插件路径，唯一标识ID
    * @Param: []
    * @return: java.lang.String
    */
    String getHandler();

    /** 
    * @Description: 描述 
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    String getDescription();

    /** 
    * @Description: 参数设置
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    List<GlobalReminderHandlerParamVo> getConfig();

    /**
     * @Description: 数据处理
     * @Param: []
     * @return: java.lang.String
     */
    Object packData(GlobalReminderMessageVo messageVo);

    /** 
    * @Description: 发送消息 
    * @Param: [] 
    * @return: void  
    */ 
     void send(ReminderMessageVo messageVo);

}

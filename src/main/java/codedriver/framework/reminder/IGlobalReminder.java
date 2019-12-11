package codedriver.framework.reminder;

import codedriver.framework.reminder.dto.GlobalReminderMessageVo;
import codedriver.framework.reminder.dto.ReminderMessageVo;
import codedriver.framework.reminder.dto.param.GlobalReminderParamVo;

import java.util.List;

/**
* @Description: 系统通知
*/ 
public interface IGlobalReminder {
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
    String getPluginId();

    /** 
    * @Description: 描述 
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    String getDescription();

    /** 
    * @Description: 内容模板路径
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    String getShowTemplate();

    /** 
    * @Description: 弹出框模板路径
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    String getPopUpTemplate();

    /** 
    * @Description: 参数设置
    * @Param: [] 
    * @return: java.lang.String  
    */ 
    List<GlobalReminderParamVo> getConfig();

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

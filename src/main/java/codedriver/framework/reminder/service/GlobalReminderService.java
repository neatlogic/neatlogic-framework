package codedriver.framework.reminder.service;

import codedriver.framework.dto.ModuleVo;
import codedriver.framework.reminder.dto.GlobalReminderMessageVo;
import codedriver.framework.reminder.dto.GlobalReminderSubscribeVo;
import codedriver.framework.reminder.dto.GlobalReminderVo;

import java.util.List;

public interface GlobalReminderService {
    
    /** 
    * @Description: 检索实时动态插件
    * @Param: [reminderVo] 
    * @return: java.util.List<com.techsure.balantflow.dto.globalreminder.GlobalReminderVo>  
    */ 
    List<GlobalReminderVo> searchReminder(GlobalReminderVo reminderVo);

    /** 
    * @Description: 获取实时动态模块集合
    * @Param: [] 
    * @return: java.util.List<com.techsure.balantflow.dto.ModuleVo>  
    */ 
    List<ModuleVo> getActiveReminderModuleList();

    /** 
    * @Description: 获取更多实时动态消息
    * @Param: [userId, messageId, day] 
    * @return: java.util.List<com.techsure.balantflow.dto.globalreminder.GlobalReminderMessageVo>  
    */ 
    List<GlobalReminderMessageVo> getDayReminderMessageVoListByUserId(String userId, Long messageId, Integer day);

    /** 
    * @Description: 获取参数天用户消息总和
    * @Param: [day, userId] 
    * @return: int  
    */ 
    int getReminderMessageCountByDay(int day, String userId);

    /** 
    * @Description: 定时获取消息集合 
    * @Param: [userId] 
    * @return: java.util.List<com.techsure.balantflow.dto.globalreminder.GlobalReminderMessageVo>  
    */ 
    List<GlobalReminderMessageVo> getScheduleMessageList(String userId);

    /** 
    * @Description: 重置用户单条消息有效性 
    * @Param: [messageId, userId] 
    * @return: void  
    */ 
    void updateMessageActive(Long messageId, String userId);

    /** 
    * @Description: 重置用户所有消息有效性 
    * @Param: [userId] 
    * @return: void  
    */ 
    void updateAllMessageActive(String userId);

    /** 
    * @Description: 重置用户当天消息有效性 
    * @Param: [userId, day] 
    * @return: void  
    */ 
    void updateDayMessageActive(String userId, int day);

    /** 
    * @Description: 获取用户实时动态消息总数 
    * @Param: [userId] 
    * @return: int  
    */ 
    int getReminderMessageCount(String userId);

    /** 
    * @Description: 重置弹窗悬浮状态
    * @Param: [userId, messageId] 
    * @return: void  
    */ 
    void updateMessageKeepStatus(String userId, Long messageId);

    /** 
    * @Description: 订阅设置开关 
    * @Param: [reminderSubscribeVo] 
    * @return: void  
    */ 
    void updateReminderSubscribe(GlobalReminderSubscribeVo reminderSubscribeVo);
}

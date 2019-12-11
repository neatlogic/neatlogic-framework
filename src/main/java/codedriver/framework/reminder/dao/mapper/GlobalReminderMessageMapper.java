package codedriver.framework.reminder.dao.mapper;

import codedriver.framework.reminder.dto.GlobalReminderMessageVo;
import codedriver.framework.reminder.dto.ReminderMessageSearchVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: balantflow
 * @description: 实时动态消息Mapper
 * @create: 2019-09-11 15:40
 **/
public interface GlobalReminderMessageMapper {

    /** 
    * @Description: 获取当天的所有用户相关消息
    * @Param: [searchVo] 
    * @return: java.util.List<com.techsure.balantflow.dto.globalreminder.GlobalReminderMessageVo>  
    */ 
    List<GlobalReminderMessageVo> getShowReminderMessageListByIdListAndUserId(ReminderMessageSearchVo searchVo);

    /** 
    * @Description: 定时获取最新消息 
    * @Param: [userId] 
    * @return: java.util.List<com.techsure.balantflow.dto.globalreminder.GlobalReminderMessageVo>  
    */ 
    List<GlobalReminderMessageVo> getScheduleMessageList(String userId);

    /** 
    * @Description: 获取当天消息数量
    * @Param: [searchVo] 
    * @return: int  
    */ 
    int getReminderMessageCountByDay(ReminderMessageSearchVo searchVo);

    /** 
    * @Description: 获取消息数量
    * @Param: [userId] 
    * @return: int  
    */ 
    int getReminderMessageCount(String userId);

    /** 
    * @Description: 插入新消息 
    * @Param: [messageVo] 
    * @return: int  
    */ 
    int insertReminderMessage(GlobalReminderMessageVo messageVo);

    /** 
    * @Description: 插入消息内容 
    * @Param: [messageVo] 
    * @return: int  
    */ 
    int insertReminderMessageContent(GlobalReminderMessageVo messageVo);

    /** 
    * @Description: 关联消息接收人 
    * @Param: [messageId, userId] 
    * @return: int  
    */ 
    int insertReminderMessageUser(@Param("messageId") Long messageId, @Param("userId") String userId);

    /** 
    * @Description: 更新新消息是否为旧消息
    * @Param: [userMessageId, userId] 
    * @return: void  
    */ 
    void updateUserMessageNewStatus(@Param("userMessageId") Long userMessageId, @Param("userId") String userId);

    /** 
    * @Description: 更新单个消息有效性
    * @Param: [messageId, userId] 
    * @return: void  
    */ 
    void updateMessageActiveById(@Param("messageId") Long messageId, @Param("userId") String userId);

    /** 
    * @Description: 更新所有消息有效性 
    * @Param: [userId] 
    * @return: void  
    */ 
    void updateAllMessageActive(@Param("userId") String userId);

    /** 
    * @Description: 跟新当天新消息有效性 
    * @Param: [searchVo] 
    * @return: void  
    */ 
    void updateDayMessageActive(ReminderMessageSearchVo searchVo);

    /** 
    * @Description: 跟新消息弹窗性 
    * @Param: [userId, messageId] 
    * @return: void  
    */ 
    void updateMessageKeepStatus(@Param("userId") String userId, @Param("messageId") Long messageId);

    /** 
    * @Description: 跟新用户插件的所有消息有效性
    * @Param: [userId, reminderId] 
    * @return: int  
    */
    int updateMessageActiveByReminderId(@Param("userId") String userId, @Param("reminderId") Long reminderId);
}

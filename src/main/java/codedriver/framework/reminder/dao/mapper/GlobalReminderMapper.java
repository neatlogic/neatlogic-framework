package codedriver.framework.reminder.dao.mapper;

import codedriver.framework.dto.ModuleVo;
import codedriver.framework.reminder.dto.GlobalReminderSubscribeVo;
import codedriver.framework.reminder.dto.GlobalReminderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GlobalReminderMapper {

    /** 
    * @Description: 重置所有实时动态插件的有效性
    * @Param: [] 
    * @return: int  
    */ 
    int resetIsActiveOfAllReminder();

    /** 
    * @Description: 检查实时动态插件是否存在
    * @Param: [name] 
    * @return: int  
    */ 
    int getReminderCountByPluginId(String pluginId);

    /** 
    * @Description: 新增实时动态插件 
    * @Param: [reminderVo] 
    * @return: int  
    */ 
    int insertReminder(GlobalReminderVo reminderVo);

    /** 
    * @Description: 根据名称更新实时动态插件
    * @Param: [reminderVo] 
    * @return: int  
    */ 
    int updateReminderByPluginId(GlobalReminderVo reminderVo);

    /** 
    * @Description: 获取订阅列表
    * @Param: [reminderVo] 
    * @return: java.util.List<com.techsure.balantflow.dto.globalreminder.GlobalReminderVo>  
    */ 
    List<GlobalReminderVo> getReminderList(GlobalReminderVo reminderVo);

    /** 
    * @Description: 获取实时动态插件的模块信息集合 
    * @Param: [] 
    * @return: java.util.List<com.techsure.balantflow.dto.ModuleVo>  
    */ 
    List<ModuleVo> getActiveReminderModuleList();

    /** 
    * @Description: 通过名称获取插件ID
    * @Param: [name] 
    * @return: java.lang.Long  
    */ 
    Long getReminderIdByName(String name);

    /** 
    * @Description: 获取用户组涉及的用户集合 
    * @Param: [teamIdList] 
    * @return: java.util.List<java.lang.String>  
    */ 
    List<String> getUserIdListByTeamIdList(@Param("teamIdList") List<Long> teamIdList);

    /** 
    * @Description: 获取插件的所有订阅用户 
    * @Param: [reminderId] 
    * @return: java.util.List<java.lang.String>  
    */ 
    List<String> getSubscribeUserIdListByReminderId(Long reminderId);

    /** 
    * @Description: 更新订阅信息（控件设置） 
    * @Param: [reminderSubscribeVo] 
    * @return: int  
    */ 
    int updateReminderSubscribe(GlobalReminderSubscribeVo reminderSubscribeVo);

    /** 
    * @Description: 取消订阅 
    * @Param: [remindSubscribeVo] 
    * @return: int  
    */ 
    int deleteReminderSubscribe(GlobalReminderSubscribeVo remindSubscribeVo);

    /** 
    * @Description: 订阅
    * @Param: [remindSubscribeVo] 
    * @return: int  
    */ 
    int insertReminderSubscribe(GlobalReminderSubscribeVo remindSubscribeVo);
}

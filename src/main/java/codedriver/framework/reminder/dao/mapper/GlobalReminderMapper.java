package codedriver.framework.reminder.dao.mapper;

import codedriver.framework.dto.ModuleVo;
import codedriver.framework.reminder.dto.GlobalReminderSubscribeVo;
import codedriver.framework.reminder.dto.GlobalReminderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GlobalReminderMapper {

    /**
    * @Description: 获取用户订阅的实时动态插件集合
    * @Param: [userId]
    * @return: java.util.List<codedriver.framework.reminder.dto.GlobalReminderSubscribeVo>
    */
    List<GlobalReminderSubscribeVo> getReminderSubscribeListByUserId(String userId);

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
    List<String> getSubscribeUserIdListByPluginId(String pluginId);

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

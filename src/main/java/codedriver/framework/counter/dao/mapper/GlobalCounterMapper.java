package codedriver.framework.counter.dao.mapper;

import codedriver.framework.dto.ModuleVo;
import codedriver.framework.counter.dto.GlobalCounterSubscribeVo;
import codedriver.framework.counter.dto.GlobalCounterUserSortVo;
import codedriver.framework.counter.dto.GlobalCounterVo;

import java.util.List;

public interface GlobalCounterMapper {

    /** 
    * @Description: 根据消息统计插件名获取插件数目
    * @Param: [name] 
    * @return: int  
    */ 
    int getCounterCountByPluginId(String pluginId);

    /** 
    * @Description: 获取用户订阅的消息统计插件集合
    * @Param: [userId] 
    * @return: java.util.List<com.techsure.balantflow.dto.globalcounter.GlobalCounterVo>  
    */ 
    List<GlobalCounterVo> getSubscribeCounterListByUserId(String userId);

    /** 
    * @Description: 获取消息统计插件集合
    * @Param: [counterVo] 
    * @return: java.util.List<com.techsure.balantflow.dto.globalcounter.GlobalCounterVo>  
    */ 
    List<GlobalCounterVo> getCounterList(GlobalCounterVo counterVo);

    /** 
    * @Description: 获取有效消息统计插件模块集合
    * @Param: [] 
    * @return: java.util.List<com.techsure.balantflow.dto.ModuleVo>  
    */ 
    List<ModuleVo> getActiveCounterModuleList();

    /** 
    * @Description: 取消消息统计插件订阅
    * @Param: [subId] 
    * @return: void  
    */ 
    void deleteCounterSubscribe(Long subId);

    /** 
    * @Description: 清空用户消息统计插件排序
    * @Param: [userId] 
    * @return: void  
    */ 
    void deleteCounterUserSortByUserId(String userId);

    /**
     * @Description: 重置所有消息统计插件有效性
     * @Param: []
     * @return: int
     */
    int resetIsActiveOfAllCounter();

    /** 
    * @Description: 通过名称更新消息统计插件
    * @Param: [counterVo] 
    * @return: int  
    */ 
    int updateCounterByPluginId(GlobalCounterVo counterVo);

    /**
     * @Description: 新增消息统计插件
     * @Param: [counterVo]
     * @return: int
     */
    int insertCounter(GlobalCounterVo counterVo);

    /** 
    * @Description: 新增用户排序 
    * @Param: [userSortVo] 
    * @return: int  
    */ 
    int insertCounterUserSort(GlobalCounterUserSortVo userSortVo);

    /** 
    * @Description: 消息统计插件订阅
    * @Param: [counterSubscribeVo] 
    * @return: int  
    */ 
    int insertCounterSubscribe(GlobalCounterSubscribeVo counterSubscribeVo);
}

package codedriver.framework.counter.dao.mapper;

import codedriver.framework.counter.dto.GlobalCounterSubscribeVo;
import codedriver.framework.counter.dto.GlobalCounterUserSortVo;
import codedriver.framework.counter.dto.GlobalCounterVo;

import java.util.List;

public interface GlobalCounterMapper {

    /** 
    * @Description: 获取用户订阅的消息统计插件集合
    * @Param: [userUuid] 
    * @return: java.util.List<com.techsure.balantflow.dto.globalcounter.GlobalCounterVo>  
    */ 
    List<GlobalCounterVo> getSubscribeCounterListByUserUuid(String userUuid);

    /**
    * @Description: 根据用户名获取counter排序
    * @Param: [userUuid]
    * @return: java.util.List<codedriver.framework.counter.dto.GlobalCounterUserSortVo>
    */
    List<GlobalCounterUserSortVo> getCounterSortListByUserUuid(String userUuid);

    /**
    * @Description:  根据用户名获取订阅插件集合
    * @Param: [userUuid]
    * @return: java.util.List<codedriver.framework.counter.dto.GlobalCounterSubscribeVo>
    */
    List<GlobalCounterSubscribeVo> getCounterSubscribeByUserUuid(String userUuid);

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
    void deleteCounterUserSortByUserUuid(String userUuid);


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

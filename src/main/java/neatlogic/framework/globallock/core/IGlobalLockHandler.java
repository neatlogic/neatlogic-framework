/*
 * Copyright (c)  2022 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.globallock.core;

import neatlogic.framework.dto.globallock.GlobalLockVo;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface IGlobalLockHandler {
    /**
     * 获取handler
     *
     * @return handler
     */
    String getHandler();

    /**
     * 获取handler name
     *
     * @return handlerName
     */
    String getHandlerName();

    /**
     * 是否允许插入锁
     *
     * @param globalLockVoList 同个key的所有数据
     * @param globalLockVo     当前需要上锁的数据
     * @return true｜false
     */
    boolean getIsCanInsertLock(List<GlobalLockVo> globalLockVoList, GlobalLockVo globalLockVo);

    /**
     * 是否允许上锁
     *
     * @param globalLockVoList 同个key的所有数据
     * @param globalLockVo     当前需要上锁的数据
     * @return true｜false
     */
    boolean getIsCanLock(List<GlobalLockVo> globalLockVoList, GlobalLockVo globalLockVo);

    /**
     * 获取锁
     *
     * @param paramJson 入参
     */
    JSONObject getLock(JSONObject paramJson);

    /**
     * 解锁 需要notify
     *
     * @param lockId    cancel lockId
     * @param paramJson 入参
     */
    JSONObject unLock(Long lockId, JSONObject paramJson);

    /**
     * 执行回调notify
     *
     * @param globalLockVo notify lock
     * @param paramJson    入参
     */
    void doNotify(GlobalLockVo globalLockVo, JSONObject paramJson);

    /**
     * 重试 获取锁
     *
     * @param lockId    cancel lockId
     * @param paramJson 入参
     */
    JSONObject retryLock(Long lockId, JSONObject paramJson);

    /**
     * 是否已经锁定
     *
     * @param paramJson
     * @return
     */
    boolean getIsBeenLocked(JSONObject paramJson);

    JSONObject getSearchResult(List<GlobalLockVo> globalLockVoList, GlobalLockVo globalLockVo);

    /**
     * 初始化搜索入参
     *
     * @param globalLockVo 入参
     */
    void initSearchParam(GlobalLockVo globalLockVo);

    /**
     * 获取是否有锁
     * @param key 唯一键
     * @return 是否存在
     */
    default boolean getIsHasLockByKey(String key){
        return false;
    };
}
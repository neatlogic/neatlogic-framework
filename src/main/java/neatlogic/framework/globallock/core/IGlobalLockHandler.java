/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.globallock.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.globallock.GlobalLockVo;

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
     * @param param 唯一键
     * @return 是否存在
     */
    default boolean getIsHasLockByKey(JSONObject param){
        return false;
    };
}

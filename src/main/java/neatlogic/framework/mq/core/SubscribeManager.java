/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.mq.core;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.exception.mq.MqHandlerNotEnableException;
import neatlogic.framework.exception.mq.MqHandlerNotFoundException;
import neatlogic.framework.exception.mq.SubscribeTopicException;
import neatlogic.framework.mq.dto.SubscribeVo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@RootComponent
public final class SubscribeManager {
    private static final Map<String, Set<SubscribeVo>> activeSubscribeMap = new HashMap<>();//记录所有激活的订阅，重连时直接从这里获取，避免反复查询数据库

    public static Map<String, Set<SubscribeVo>> getActiveSubscribeMap() {
        return activeSubscribeMap;
    }

    public static void reconnect(SubscribeVo subscribeVo) throws SubscribeTopicException, ExecutionException, InterruptedException {
        IMqHandler handler = MqHandlerFactory.getMqHandler(subscribeVo.getHandler());
        if (handler != null) {
            handler.reconnect(subscribeVo);
        } else {
            throw new MqHandlerNotFoundException(subscribeVo.getHandler());
        }
    }


    /**
     * 删除订阅
     */
    public static void destroy(SubscribeVo subVo) {
        IMqHandler handler = MqHandlerFactory.getMqHandler(subVo.getHandler());
        if (handler != null) {
            handler.destroy(subVo);
        }
        if (activeSubscribeMap.containsKey(TenantContext.get().getTenantUuid())) {
            activeSubscribeMap.get(TenantContext.get().getTenantUuid()).removeIf(d -> d.getId().equals(subVo.getId()));
        }
    }

    public static boolean needReconnect(SubscribeVo subVo) {
        IMqHandler handler = MqHandlerFactory.getMqHandler(subVo.getHandler());
        if (handler != null) {
            if (handler.isEnable()) {
                return !handler.isRunning(subVo);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 创建订阅
     *
     * @return 是否成功
     */
    public static void create(SubscribeVo subVo) throws SubscribeTopicException, ExecutionException, InterruptedException {
        IMqHandler handler = MqHandlerFactory.getMqHandler(subVo.getHandler());
        if (handler == null) {
            throw new MqHandlerNotFoundException(subVo.getHandler());
        }
        if (handler.isEnable()) {
            /*if (!TopicFactory.hasTopic(subVo.getTopicName())) {
                throw new TopicNotFoundException(subVo.getTopicName());
            }*/
            //不管是否成功添加，都需要加入activeSubscribeMap，重连机制会从这里取数重连
            if (!activeSubscribeMap.containsKey(TenantContext.get().getTenantUuid())) {
                activeSubscribeMap.put(TenantContext.get().getTenantUuid(), new HashSet<>());
            }
            activeSubscribeMap.get(TenantContext.get().getTenantUuid()).add(subVo);
            handler.create(subVo);
        } else {
            throw new MqHandlerNotEnableException(handler.getLabel());
        }
    }
}

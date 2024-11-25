/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.mq.core;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.exception.mq.MqHandlerNotEnableException;
import neatlogic.framework.exception.mq.MqHandlerNotFoundException;
import neatlogic.framework.exception.mq.SubscribeTopicException;
import neatlogic.framework.mq.dto.SubscribeVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RootComponent
public final class SubscribeManager {
    private static final Map<String, List<SubscribeVo>> activeSubscribeMap = new HashMap<>();//记录所有激活的订阅，重连时直接从这里获取，避免反复查询数据库

    public static Map<String, List<SubscribeVo>> getActiveSubscribeMap() {
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
                activeSubscribeMap.put(TenantContext.get().getTenantUuid(), new ArrayList<>());
            }
            activeSubscribeMap.get(TenantContext.get().getTenantUuid()).add(subVo);
            handler.create(subVo);
        } else {
            throw new MqHandlerNotEnableException(handler.getLabel());
        }
    }
}

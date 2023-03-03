/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.heartbeat.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.heartbeat.dao.mapper.ServerMapper;
import neatlogic.framework.heartbeat.dto.ServerClusterVo;
import neatlogic.framework.transaction.util.TransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RootComponent
public class HeartbeatManager extends ModuleInitializedListenerBase {
    private final Logger logger = LoggerFactory.getLogger(HeartbeatManager.class);
    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private TransactionUtil transactionUtil;//强迫TransactionUtil先加载，否则可能会出现空指针

    private static final Set<IHeartbreakHandler> set = new HashSet<>();

    public final void myInit() {
        // 服务器重启时，先重置与自己相关的数据
        getServerLock(Config.SCHEDULE_SERVER_ID);
        // 重新插入一条服务器信息
        ServerClusterVo server = new ServerClusterVo(Config.SERVER_HOST, Config.SCHEDULE_SERVER_ID, ServerClusterVo.STARTUP);
        serverMapper.insertServer(server);
        ScheduledExecutorService heartbeatService = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        NeatLogicThread runnable = new NeatLogicThread("HEARTBEAT") {
            @Override
            protected void execute() {
                try {
                    // 查找故障服务器
                    List<ServerClusterVo> list = serverMapper.getInactivatedServer(Config.SCHEDULE_SERVER_ID, Config.SERVER_HEARTBEAT_THRESHOLD());
                    for (ServerClusterVo server : list) {
                        if (getServerLock(server.getServerId())) {
                            // 如果抢到锁，开始处理
                            for (IHeartbreakHandler observer : set) {
                                CachedThreadPool.execute(new HeartbreakHandlerThread(observer, server.getServerId()));
                            }
                        }
                    }
                    // 将自己的计数器清零
                    serverMapper.resetCounterByToServerId(Config.SCHEDULE_SERVER_ID);
                    // 查出正常服务器及计数器加一后的值
                    serverMapper.updateServerCounterIncrementByOneByFromServerId(Config.SCHEDULE_SERVER_ID);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };
        heartbeatService.scheduleAtFixedRate(runnable, Config.SERVER_HEARTBEAT_RATE(), Config.SERVER_HEARTBEAT_RATE(), TimeUnit.MINUTES);
    }

    /**
     * @param serverId 故障服务器id
     * @return boolean
     * @Description: 将故障服务器状态设置为停止，删除与该服务器相关的计数器数据
     */
    public boolean getServerLock(Integer serverId) {
        TransactionStatus transactionStatus = TransactionUtil.openTx();
        boolean returnVal = false;
        try {
            ServerClusterVo serverVo = serverMapper.getServerByServerId(serverId);
            if (serverVo != null) {
                if (ServerClusterVo.STARTUP.equals(serverVo.getStatus())) {
                    serverVo.setStatus(ServerClusterVo.STOP);
                    serverMapper.updateServerByServerId(serverVo);
                    returnVal = true;
                }
            }
            TransactionUtil.commitTx(transactionStatus);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            TransactionUtil.rollbackTx(transactionStatus);
        }
        if (returnVal) {
            // `server_counter`是内存表，不支持事务，不能与其他支持事务的表在同个事务里更新数据
            serverMapper.deleteCounterByServerId(serverId);
        }
        return returnVal;
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        // 找出所有实现ServerObserver接口的类
        Map<String, IHeartbreakHandler> serverObserverMap = context.getBeansOfType(IHeartbreakHandler.class);
        for (Entry<String, IHeartbreakHandler> entry : serverObserverMap.entrySet()) {
            set.add(entry.getValue());
        }
    }

}

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

package neatlogic.framework.heartbeat.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.heartbeat.dao.mapper.ServerMapper;
import neatlogic.framework.heartbeat.dao.mapper.TenantServerMapper;
import neatlogic.framework.heartbeat.dto.ServerClusterVo;
import neatlogic.framework.heartbeat.dto.ServerCounterVo;
import neatlogic.framework.transaction.util.TransactionUtil;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RootComponent
public class HeartbeatManager extends ModuleInitializedListenerBase {
    private final Logger logger = LoggerFactory.getLogger(HeartbeatManager.class);

    // 记录服务器启动时间
    public static final Date START_TIME = new Date();

    @Resource
    private ServerMapper serverMapper;

    @Resource
    private TenantServerMapper tenantServerMapper;

    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private TransactionUtil transactionUtil;//强迫TransactionUtil先加载，否则可能会出现空指针

    private static final Set<IHeartbreakHandler> set = new HashSet<>();

    public final void myInit() {
        String ip = StringUtils.EMPTY;
        if (Objects.equals(Config.SCHEDULE_SERVER_ID_CHECK_ENABLE(), 1)) {
            String userFunctionValue = serverMapper.getUserFunctionValue(); // 获取到 root@192.168.8.240
            if (StringUtils.isNotBlank(userFunctionValue)) {
                int index = userFunctionValue.indexOf("@");
                if (index != -1) {
                    ip = userFunctionValue.substring(index + 1);
                } else {
                    ip = userFunctionValue;
                }
            }
            if (Objects.equals(ip, "localhost")) {
                ip = "127.0.0.1";
            }
            ServerClusterVo serverVo = serverMapper.getServerLockByServerId(Config.SCHEDULE_SERVER_ID);
            if (serverVo != null && StringUtils.isNotBlank(serverVo.getIp())) {
                String oldIp = serverVo.getIp();
                if (Objects.equals(oldIp, "localhost")) {
                    oldIp = "127.0.0.1";
                }
                if (!Objects.equals(oldIp, ip) && Objects.equals(serverVo.getStatus(), ServerClusterVo.STARTUP)) {
                    System.err.println($.t("nfhc.heartbeatmanager.myinit.startupfailureprompt", Config.SCHEDULE_SERVER_ID, serverVo.getIp()));
                    System.exit(1);
                }
            }
        }
        // 服务器重启时，先重置与自己相关的数据
        getServerLock(Config.SCHEDULE_SERVER_ID);
        // 重新插入一条服务器信息
        ServerClusterVo server = new ServerClusterVo();
        server.setServerId(Config.SCHEDULE_SERVER_ID);
        server.setStatus(ServerClusterVo.STARTUP);
        server.setFcu(SystemUser.SYSTEM.getUserUuid());
        server.setLcu(SystemUser.SYSTEM.getUserUuid());
        server.setHeartbeatRate(Config.SERVER_HEARTBEAT_RATE());
        server.setHeartbeatThreshold(Config.SERVER_HEARTBEAT_THRESHOLD());
        server.setIp(ip);
        server.setStartTime(START_TIME);
        server.setServerGroup(Config.SCHEDULE_SERVER_GROUP());
        serverMapper.insertServer(server);
//        serverMapper.insertServerRunTime(Config.SCHEDULE_SERVER_ID, START_TIME);
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
                    List<Integer> sameGroupServerIdList = getSameGroupStartupServerIdList();
                    List<Integer> serverIdList = serverMapper.getInactivatedServerIdList(Config.SCHEDULE_SERVER_ID, Config.SERVER_HEARTBEAT_THRESHOLD(), sameGroupServerIdList);
                    for (Integer serverId : serverIdList) {
                        if (getServerLock(serverId)) {
                            // 如果抢到锁，开始处理
                            for (IHeartbreakHandler observer : set) {
                                CachedThreadPool.execute(new HeartbreakHandlerThread(observer, serverId));
                            }
                        }
                    }
                    // 将自己的计数器清零
                    serverMapper.resetCounterByToServerId(Config.SCHEDULE_SERVER_ID);
                    // 查出正常服务器及计数器加一后的值
                    List<ServerClusterVo> serverList = serverMapper.getAllServerList();
//                    List<ServerClusterVo> serverList = serverMapper.getAllServerListByGroup(Config.SCHEDULE_SERVER_GROUP());
                    for (ServerClusterVo serverClusterVo : serverList) {
                        if (Objects.equals(serverClusterVo.getServerId(), Config.SCHEDULE_SERVER_ID)) {
                            continue;
                        }
                        if (Objects.equals(serverClusterVo.getStatus(), ServerClusterVo.STARTUP)) {
                            ServerCounterVo serverCounterVo = new ServerCounterVo();
                            serverCounterVo.setFromServerId(Config.SCHEDULE_SERVER_ID);
                            serverCounterVo.setToServerId(serverClusterVo.getServerId());
                            serverCounterVo.setCounter(1);
                            serverMapper.insertServerCounter(serverCounterVo);
                        }
                    }
                    serverMapper.updateServerHeartbeatTimeByServerId(Config.SCHEDULE_SERVER_ID);
//                    serverMapper.insertServerRunTime(Config.SCHEDULE_SERVER_ID, START_TIME);
//                    insertTenantServerRunTime();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };
        runnable.setNeedAwaitAdvance(false);
        heartbeatService.scheduleAtFixedRate(runnable, Config.SERVER_HEARTBEAT_RATE(), Config.SERVER_HEARTBEAT_RATE(), TimeUnit.SECONDS);
    }

    /**
     * @param serverId 故障服务器id
     * @return boolean
     * @Description: 将故障服务器状态设置为停止，删除与该服务器相关的计数器数据
     */
    private boolean getServerLock(Integer serverId) {
        TransactionStatus transactionStatus = TransactionUtil.openTx();
        boolean returnVal = false;
        try {
            ServerClusterVo serverVo = serverMapper.getServerLockByServerId(serverId);
            if (serverVo != null) {
//                if (!Objects.equals(serverId, Config.SCHEDULE_SERVER_ID)
//                        && !Objects.equals(serverVo.getServerGroup(), Config.SCHEDULE_SERVER_GROUP())) {
//                    TransactionUtil.commitTx(transactionStatus);
//                    return false;
//                }
                if (ServerClusterVo.STARTUP.equals(serverVo.getStatus())) {
                    serverVo.setStatus(ServerClusterVo.STOP);
                    serverVo.setFcu(SystemUser.SYSTEM.getUserUuid());
                    serverVo.setLcu(SystemUser.SYSTEM.getUserUuid());
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
            serverMapper.deleteCounterByToServerId(serverId);
        }
        return returnVal;
    }

    private List<Integer> getSameGroupStartupServerIdList() {
        List<Integer> serverIdList = serverMapper.getStartupServerIdListByGroup(Config.SCHEDULE_SERVER_GROUP());
//        if (serverIdList == null) {
//            serverIdList = new ArrayList<>();
//        }
        if (!serverIdList.contains(Config.SCHEDULE_SERVER_ID)) {
            serverIdList.add(Config.SCHEDULE_SERVER_ID);
        }
        return serverIdList;
    }

    private void insertTenantServerRunTime() {
        List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();
        for (TenantVo tenantVo : tenantList) {
            TenantContext.get().switchTenant(tenantVo.getUuid());
            tenantServerMapper.insertTenantServerRunTime(Config.SCHEDULE_SERVER_ID, START_TIME);
        }
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

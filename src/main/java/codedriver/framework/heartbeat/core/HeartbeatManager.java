/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.heartbeat.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.heartbeat.dao.mapper.ServerMapper;
import codedriver.framework.heartbeat.dto.ServerClusterVo;
import codedriver.framework.heartbeat.dto.ServerCounterVo;
import codedriver.framework.transaction.util.TransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
        String ip = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        ServerClusterVo server = new ServerClusterVo(ip, Config.SCHEDULE_SERVER_ID, ServerClusterVo.STARTUP);
        serverMapper.insertServer(server);
        ScheduledExecutorService heartbeatService = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        CodeDriverThread runnable = new CodeDriverThread("HEARTBEAT") {
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
    public void onInitialized(CodedriverWebApplicationContext context) {
        // 找出所有实现ServerObserver接口的类
        Map<String, IHeartbreakHandler> serverObserverMap = context.getBeansOfType(IHeartbreakHandler.class);
        for (Entry<String, IHeartbreakHandler> entry : serverObserverMap.entrySet()) {
            set.add(entry.getValue());
        }
    }

}

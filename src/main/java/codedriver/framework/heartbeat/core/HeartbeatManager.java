package codedriver.framework.heartbeat.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.heartbeat.dao.mapper.ServerMapper;
import codedriver.framework.heartbeat.dto.ServerClusterVo;
import codedriver.framework.heartbeat.dto.ServerCounterVo;

@RootComponent
public class HeartbeatManager extends ApplicationListenerBase  {
	private Logger logger = LoggerFactory.getLogger(HeartbeatManager.class);
	@Autowired
	private ServerMapper serverMapper;
	@Autowired
	private DataSourceTransactionManager dataSourceTransactionManager;

	private static Set<IHeartbreakHandler> set = new HashSet<>();

	public final void myInit() {
		// 服务器重启时，先重置与自己相关的数据
		getServerLock(Config.SCHEDULE_SERVER_ID);
		// 重新插入一条服务器信息
		ServerClusterVo server = new ServerClusterVo(null, Config.SCHEDULE_SERVER_ID, ServerClusterVo.STARTUP);
		serverMapper.replaceServer(server);
		ScheduledExecutorService heartbeatService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				t.setName("HEARTBEAT");
				return t;
			}
		});
		CodeDriverThread runnable = new CodeDriverThread() {
			@Override
			protected void execute() {
				try {
					// 查找故障服务器
					List<ServerClusterVo> list = serverMapper.getInactivatedServer(Config.SCHEDULE_SERVER_ID, Config.SERVER_HEARTBEAT_THRESHOLD);
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
					List<ServerCounterVo> serverCounterList = serverMapper.getServerCounterIncreaseByFromServerId(Config.SCHEDULE_SERVER_ID);
					for (ServerCounterVo serverCounter : serverCounterList) {
						// 重新插入数据
						serverMapper.replaceServerCounter(serverCounter);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		};
		heartbeatService.scheduleAtFixedRate(runnable, Config.SERVER_HEARTBEAT_RATE, Config.SERVER_HEARTBEAT_RATE, TimeUnit.MINUTES);
	}

	/**
	 * 
	 * @Description: 将故障服务器状态设置为停止，删除与该服务器相关的计数器数据
	 * @param serverId
	 *            故障服务器id
	 * @return boolean
	 */
	public boolean getServerLock(Integer serverId) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(def);
		boolean returnVal = false;
		try {
			ServerClusterVo serverVo = serverMapper.getServerByServerId(serverId);
			if (serverVo != null) {
				if (ServerClusterVo.STARTUP.equals(serverVo.getStatus())) {
					serverVo.setStatus(ServerClusterVo.STOP);
					serverMapper.updateServerByServerId(serverVo);
					serverMapper.deleteCounterByServerId(serverVo.getServerId());
					returnVal = true;
				}
			}
			dataSourceTransactionManager.commit(transactionStatus);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			dataSourceTransactionManager.rollback(transactionStatus);
		}
		return returnVal;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		// 找出所有实现ServerObserver接口的类
		Map<String, IHeartbreakHandler> serverObserverMap = context.getBeansOfType(IHeartbreakHandler.class);
		for (Entry<String, IHeartbreakHandler> entry : serverObserverMap.entrySet()) {
			set.add(entry.getValue());
		}
	}

}

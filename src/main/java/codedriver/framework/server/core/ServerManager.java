package codedriver.framework.server.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.server.dao.mapper.ServerMapper;
import codedriver.framework.server.dto.ServerClusterVo;
@RootComponent
public class ServerManager implements ApplicationListener<ContextRefreshedEvent>{
	private Logger logger = LoggerFactory.getLogger(ServerManager.class);
	@Autowired
	private ServerMapper serverMapper;
	@Autowired
	private DataSourceTransactionManager dataSourceTransactionManager;
	
	private static Set<ServerObserver> set = new HashSet<>();
	
	@PostConstruct
	public final void init() {
		System.out.println("心跳启动");
		if(getServerLock(Config.SCHEDULE_SERVER_ID)) {
			for(ServerObserver observer : set) {
				CommonThreadPool.execute(new ServerObserverThread(observer, Config.SCHEDULE_SERVER_ID));
			}
		}
		ServerClusterVo server = new ServerClusterVo(null, Config.SCHEDULE_SERVER_ID, ServerClusterVo.STARTUP);
		serverMapper.insertServer(server);
		ScheduledExecutorService heartbeatService = Executors.newScheduledThreadPool(1);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				try {
					System.out.println("一次心跳开始");
					List<ServerClusterVo> list = serverMapper.getInactivatedServer(Config.SCHEDULE_SERVER_ID, Config.SERVER_HEARTBEAT_THRESHOLD);					
					for(ServerClusterVo server : list) {						
						if(getServerLock(server.getServerId())) {
							for(ServerObserver observer : set) {
								CommonThreadPool.execute(new ServerObserverThread(observer, server.getServerId()));
							}
						}						
					}
					serverMapper.resetCounterByToServerId(Config.SCHEDULE_SERVER_ID);
					List<ServerClusterVo> startupServerList = serverMapper.getServerByStatus(ServerClusterVo.STARTUP);
					for(ServerClusterVo server : startupServerList) {
						int serverId = server.getServerId();
						if(serverId == Config.SCHEDULE_SERVER_ID) {
							continue;
						}
						int count = serverMapper.counterIncrease(Config.SCHEDULE_SERVER_ID, serverId);
						if(count == 0) {
							serverMapper.insertServerCounter(Config.SCHEDULE_SERVER_ID, serverId);
						}
					}
					System.out.println("一次心跳结束");
				}catch(Exception e) {
					logger.error(e.getMessage(),e);
				}
				
			}		
		};
		heartbeatService.scheduleAtFixedRate(runnable, 1, Config.SERVER_HEARTBEAT_RATE, TimeUnit.MINUTES);
	}
	
	public boolean getServerLock(Integer serverId) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(def);
		try {
			ServerClusterVo serverVo = serverMapper.getServerByServerId(serverId);
			if(ServerClusterVo.STARTUP.equals(serverVo.getStatus())) {
				serverVo.setStatus(ServerClusterVo.STOP);
				serverMapper.updateServerByServerId(serverVo);
				serverMapper.deleteCounterByServerId(serverVo.getServerId());
			}
			dataSourceTransactionManager.commit(transactionStatus);
			return true;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			dataSourceTransactionManager.rollback(transactionStatus);
		}
		return false;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, ServerObserver> map = context.getBeansOfType(ServerObserver.class);
		for(Entry<String, ServerObserver> entry : map.entrySet()) {
			set.add(entry.getValue());
		}
	}

}

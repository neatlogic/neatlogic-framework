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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.server.dao.mapper.ServerMapper;
import codedriver.framework.server.dto.ServerClusterVo;
@RootComponent
public class ServerManager implements ApplicationListener<ContextRefreshedEvent>{
	
	@Autowired
	private ServerMapper serverMapper;
	
	private static Set<ServerObserver> set = new HashSet<>();
	
	@PostConstruct
	public final void init() {
		ServerClusterVo server = new ServerClusterVo(null, Config.SCHEDULE_SERVER_ID, ServerClusterVo.STARTUP);
		serverMapper.insertServer(server);
		ScheduledExecutorService heartbeatService = Executors.newScheduledThreadPool(1);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				List<ServerClusterVo> list = serverMapper.getInactivatedServer(Config.SCHEDULE_SERVER_ID, Config.SERVER_HEARTBEAT_THRESHOLD);
				for(ServerClusterVo server : list) {
					server.setStatus(ServerClusterVo.STOP);
					
					int count = serverMapper.updateServerByServerId(server);
					if(count == 1) {
						serverMapper.deleteCounterByServerId(server.getServerId());
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
				
			}		
		};
		heartbeatService.scheduleAtFixedRate(runnable, 1, Config.SERVER_HEARTBEAT_RATE, TimeUnit.MINUTES);
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

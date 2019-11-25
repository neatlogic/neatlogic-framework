package codedriver.framework.server;

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

import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.server.dao.mapper.ServerMapper;
import codedriver.framework.server.dto.ServerClusterVo;
import codedriver.framework.server.dto.ServerNewJobVo;
@RootComponent
public class ServerManager implements ApplicationListener<ContextRefreshedEvent>{

	private Logger logger = LoggerFactory.getLogger(ServerManager.class);
	@Autowired
	private ServerMapper serverMapper;
	
	private static Set<ServerObserver> set = new HashSet<>();
	
	@PostConstruct
	public final void init() {
		ServerClusterVo server = new ServerClusterVo(null, 1, ServerClusterVo.STARTUP);
		serverMapper.insertServer(server);
		ScheduledExecutorService heartbeatService = Executors.newScheduledThreadPool(1);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				List<ServerClusterVo> list = serverMapper.getInactivatedServer(1, 5);
				for(ServerClusterVo server : list) {
					server.setStatus(ServerClusterVo.STOP);
					
					int count = serverMapper.updateServerByServerId(server);
					if(count == 1) {
						serverMapper.deleteCounterByServerId(server.getServerId());
						for(ServerObserver observer : set) {
							observer.whenServerInactivated(server.getServerId());
						}
					}
				}
				serverMapper.resetCounterByToServerId(1);//Config.SCHEDULE_SERVER_ID
				List<ServerClusterVo> startupServerList = serverMapper.getServerByStatus(ServerClusterVo.STARTUP);
				for(ServerClusterVo server : startupServerList) {
					int count = serverMapper.counterIncrease(1, server.getServerId());
					if(count == 0) {
						serverMapper.insertServerCounter(1, server.getServerId());
					}
				}
				//TODO 检查newjob
				List<ServerNewJobVo> newJobList = serverMapper.getNewJobByServerId(1);
				for(ServerNewJobVo newJob : newJobList) {
					//TODO 加载newJob
				}
			}		
		};
		heartbeatService.scheduleAtFixedRate(runnable, 1, 3, TimeUnit.MINUTES);
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

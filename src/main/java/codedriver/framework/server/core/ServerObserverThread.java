package codedriver.framework.server.core;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;

public class ServerObserverThread extends CodeDriverThread {

	private ServerObserver observer;
	private Integer serverId;
	
	public ServerObserverThread(ServerObserver observer, Integer serverId) {
		this.observer = observer;
		this.serverId = serverId;
	}

	@Override
	protected void execute() {
		if(tenantContext == null) {
			tenantContext = TenantContext.init();
		}
		observer.whenServerInactivated(serverId);		
	}

}

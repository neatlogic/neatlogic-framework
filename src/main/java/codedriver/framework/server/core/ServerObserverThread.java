package codedriver.framework.server.core;

import codedriver.framework.asynchronization.thread.CodeDriverThread;

public class ServerObserverThread extends CodeDriverThread {

	private ServerObserver observer;
	private Integer serverId;
	
	public ServerObserverThread(ServerObserver observer, Integer serverId) {
		this.observer = observer;
		this.serverId = serverId;
	}

	@Override
	protected void execute() {
		observer.whenServerInactivated(serverId);		
	}

}

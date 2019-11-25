package codedriver.framework.server.core;

public class ServerObserverThread implements Runnable{

	private ServerObserver observer;
	private Integer serverId;
	
	public ServerObserverThread(ServerObserver observer, Integer serverId) {
		this.observer = observer;
		this.serverId = serverId;
	}
	
	@Override
	public void run() {
		observer.whenServerInactivated(serverId);		
	}

}

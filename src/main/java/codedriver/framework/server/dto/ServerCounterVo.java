package codedriver.framework.server.dto;

public class ServerCounterVo {

	private Integer fromServerId;
	private Integer toServerId;
	private Integer counter;
	public Integer getFromServerId() {
		return fromServerId;
	}
	public void setFromServerId(Integer fromServerId) {
		this.fromServerId = fromServerId;
	}
	public Integer getToServerId() {
		return toServerId;
	}
	public void setToServerId(Integer toServerId) {
		this.toServerId = toServerId;
	}
	public Integer getCounter() {
		return counter;
	}
	public void setCounter(Integer counter) {
		this.counter = counter;
	}
}

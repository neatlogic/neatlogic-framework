package codedriver.framework.notify.dto;

public class NotifyReceiverVo {
	private String type;
	private String uuid;
	public NotifyReceiverVo() {
	}
	public NotifyReceiverVo(String type, String uuid) {
		this.type = type;
		this.uuid = uuid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}

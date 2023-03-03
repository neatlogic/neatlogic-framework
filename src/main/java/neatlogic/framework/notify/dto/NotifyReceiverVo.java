package neatlogic.framework.notify.dto;

/**
 * 
* @Author:14378
* @Time:2020年7月3日
* @ClassName: NotifyReceiverVo 
* @Description: 通知接收对象
 */
public class NotifyReceiverVo {
	/** 对象类型，用户、组、角色 **/
	private String type;
	/** 对象uuid **/
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

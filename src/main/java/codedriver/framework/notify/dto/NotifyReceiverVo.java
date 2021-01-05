package codedriver.framework.notify.dto;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NotifyReceiverVo that = (NotifyReceiverVo) o;
		return Objects.equals(type, that.type) && Objects.equals(uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
}

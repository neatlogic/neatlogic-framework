package neatlogic.framework.notify.dto.job;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

public class NotifyJobReceiverVo{
	@EntityField(name = "通知定时任务ID", type = ApiParamType.LONG)
	private Long notifyJobId;
	@EntityField(name = "接收者、组、角色uuid或者邮箱", type = ApiParamType.STRING)
	private String receiver;
	@EntityField(name = "receiver类型，区分是用户、组、角色的uuid还是邮箱", type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "接收者类型，to:收件人;cc:抄送人", type = ApiParamType.STRING)
	private String receiveType;

	public Long getNotifyJobId() {
		return notifyJobId;
	}

	public void setNotifyJobId(Long notifyJobId) {
		this.notifyJobId = notifyJobId;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReceiveType() {
		return receiveType;
	}

	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}
}

package codedriver.framework.notify.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.notify.constvalue.NotifyPolicyActionType;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

public class NotifyTemplateVo extends BasePageVo {

	@EntityField(name = "模板id", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "标题", type = ApiParamType.STRING)
	private String title;
	@EntityField(name = "内容", type = ApiParamType.STRING)
	private String content;
//	private String type;
//	private int isReadOnly = 0;
//	private String notifyHandlerType;
//	private String notifyHandlerTypeText;
//	private String trigger;
//	private String triggerText;
	@EntityField(name = "通知处理器类型", type = ApiParamType.STRING)
	private String notifyHandler;
	
	@EntityField(name = "操作用户", type = ApiParamType.STRING)
	private String actionUser;
	@EntityField(name = "操作时间", type = ApiParamType.LONG)
	private Date actionTime;
	@EntityField(name = "操作类型，create|update", type = ApiParamType.STRING)
	private String action;
	@EntityField(name = "操作类型名，创建|修改", type = ApiParamType.STRING)
	private String actionName;
	
//	private transient String fcu;
//	private transient String lcu;

	public NotifyTemplateVo() {
	}

//	public NotifyTemplateVo(Long id, String name, String type, Integer isReadOnly, String notifyHandlerType, String trigger, String title, String content) {
//		this.id = id;
//		this.name = name;
//		this.title = title;
//		this.content = content;
//		this.type = type;
//		this.isReadOnly = isReadOnly;
//		this.notifyHandlerType = notifyHandlerType;
//		this.trigger = trigger;
//	}

	public Long getId() {
		if(id == null) {
			id = SnowflakeUtil.uniqueLong();
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}

//	public String getFcu() {
//		return fcu;
//	}
//
//	public void setFcu(String fcu) {
//		this.fcu = fcu;
//	}
//
//	public String getLcu() {
//		return lcu;
//	}
//
//	public void setLcu(String lcu) {
//		this.lcu = lcu;
//	}
//
//	public int getIsReadOnly() {
//		return isReadOnly;
//	}
//
//	public void setIsReadOnly(int isReadOnly) {
//		this.isReadOnly = isReadOnly;
//	}
//
//	public String getNotifyHandlerType() {
//		if(StringUtils.isBlank(notifyHandlerType) && StringUtils.isNotBlank(notifyHandler)) {
//			INotifyHandler handler = NotifyHandlerFactory.getHandler(notifyHandler);
//			if (handler != null) {
//				notifyHandlerType = handler.getType();
//			}
//		}
//		return notifyHandlerType;
//	}
//
//	public void setNotifyHandlerType(String notifyHandlerType) {	
//		this.notifyHandlerType = notifyHandlerType;
//	}
//
//	public String getTrigger() {
//		return trigger;
//	}
//
//	public void setTrigger(String trigger) {
//		this.trigger = trigger;
//	}
//
//	public String getNotifyHandlerTypeText() {
//		if(StringUtils.isBlank(notifyHandlerTypeText) && StringUtils.isNotBlank(getNotifyHandlerType())) {
//			notifyHandlerTypeText = NotifyHandlerType.getText(getNotifyHandlerType());
//		}
//		return notifyHandlerTypeText;
//	}
//
//	public void setNotifyHandlerTypeText(String notifyHandlerTypeText) {
//		this.notifyHandlerTypeText = notifyHandlerTypeText;
//	}
//
//	public String getTriggerText() {
////		if(StringUtils.isBlank(triggerText) && StringUtils.isNotBlank(trigger)) {
////			triggerText = NotifyTriggerType.getText(trigger);
////		}
//		return triggerText;
//	}
//
//	public void setTriggerText(String triggerText) {
//		this.triggerText = triggerText;
//	}

	public String getNotifyHandler() {
//		if(StringUtils.isBlank(notifyHandler) && StringUtils.isNotBlank(notifyHandlerType)) {
//			for(ValueTextVo valueTextVo : NotifyHandlerFactory.getNotifyHandlerTypeList()) {
//				INotifyHandler handler = NotifyHandlerFactory.getHandler(valueTextVo.getValue());
//				if(Objects.equal(handler.getType(), notifyHandlerType)) {
//					notifyHandler = valueTextVo.getValue();
//				}
//			}
//		}
		return notifyHandler;
	}

	public void setNotifyHandler(String notifyHandler) {
		this.notifyHandler = notifyHandler;
	}

	public String getActionUser() {
		return actionUser;
	}

	public void setActionUser(String actionUser) {
		this.actionUser = actionUser;
	}

	public Date getActionTime() {
		return actionTime;
	}

	public void setActionTime(Date actionTime) {
		this.actionTime = actionTime;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionName() {
		if(StringUtils.isBlank(actionName) && StringUtils.isNotBlank(action)) {
			actionName = NotifyPolicyActionType.getText(action);
		}
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
}

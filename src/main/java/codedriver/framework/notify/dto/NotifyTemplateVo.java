package codedriver.framework.notify.dto;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Objects;

import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.core.INotifyHandler;
import codedriver.framework.notify.core.NotifyHandlerFactory;
import codedriver.framework.notify.core.NotifyHandlerType;
import codedriver.framework.util.SnowflakeUtil;

public class NotifyTemplateVo extends BasePageVo {

	private Long id;
	private String name;
	private String title;
	private String content;
	private String type;
	private int isReadOnly = 0;
	private String notifyHandlerType;
	private String notifyHandlerTypeText;
	private String trigger;
	private String triggerText;
	private String notifyHandler;
	
	private transient String fcu;
	private transient String lcu;
	
	private transient String keyword;

	public NotifyTemplateVo() {
	}

	public NotifyTemplateVo(Long id, String name, String type, Integer isReadOnly, String notifyHandlerType, String trigger, String title, String content) {
		this.id = id;
		this.name = name;
		this.title = title;
		this.content = content;
		this.type = type;
		this.isReadOnly = isReadOnly;
		this.notifyHandlerType = notifyHandlerType;
		this.trigger = trigger;
	}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getFcu() {
		return fcu;
	}

	public void setFcu(String fcu) {
		this.fcu = fcu;
	}

	public String getLcu() {
		return lcu;
	}

	public void setLcu(String lcu) {
		this.lcu = lcu;
	}

	public int getIsReadOnly() {
		return isReadOnly;
	}

	public void setIsReadOnly(int isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public String getNotifyHandlerType() {
		if(StringUtils.isBlank(notifyHandlerType) && StringUtils.isNotBlank(notifyHandler)) {
			INotifyHandler handler = NotifyHandlerFactory.getHandler(notifyHandler);
			if (handler != null) {
				notifyHandlerType = handler.getType();
			}
		}
		return notifyHandlerType;
	}

	public void setNotifyHandlerType(String notifyHandlerType) {	
		this.notifyHandlerType = notifyHandlerType;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public String getNotifyHandlerTypeText() {
		if(StringUtils.isBlank(notifyHandlerTypeText) && StringUtils.isNotBlank(getNotifyHandlerType())) {
			notifyHandlerTypeText = NotifyHandlerType.getText(getNotifyHandlerType());
		}
		return notifyHandlerTypeText;
	}

	public void setNotifyHandlerTypeText(String notifyHandlerTypeText) {
		this.notifyHandlerTypeText = notifyHandlerTypeText;
	}

	public String getTriggerText() {
//		if(StringUtils.isBlank(triggerText) && StringUtils.isNotBlank(trigger)) {
//			triggerText = NotifyTriggerType.getText(trigger);
//		}
		return triggerText;
	}

	public void setTriggerText(String triggerText) {
		this.triggerText = triggerText;
	}

	public String getNotifyHandler() {
		if(StringUtils.isBlank(notifyHandler) && StringUtils.isNotBlank(notifyHandlerType)) {
			for(ValueTextVo valueTextVo : NotifyHandlerFactory.getNotifyHandlerTypeList()) {
				INotifyHandler handler = NotifyHandlerFactory.getHandler(valueTextVo.getValue());
				if(Objects.equal(handler.getType(), notifyHandlerType)) {
					notifyHandler = valueTextVo.getValue();
				}
			}
		}
		return notifyHandler;
	}

	public void setNotifyHandler(String notifyHandler) {
		this.notifyHandler = notifyHandler;
	}
}

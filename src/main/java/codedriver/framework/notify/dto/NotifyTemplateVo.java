package codedriver.framework.notify.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.constvalue.ActionType;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
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
	/** 不作为数据库与显示字段，仅为排序使用 */
	private Long lcd = 0L;

	public NotifyTemplateVo() {
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

	public String getNotifyHandler() {
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
			actionName = ActionType.getText(action);
		}
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public Long getLcd() {
		return lcd;
	}

	public void setLcd(Long lcd) {
		this.lcd = lcd;
	}
}

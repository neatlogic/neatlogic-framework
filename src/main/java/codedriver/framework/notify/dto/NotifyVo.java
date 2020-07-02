package codedriver.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dto.UserVo;
import codedriver.framework.notify.core.INotifyTriggerType;
import codedriver.framework.util.FreemarkerUtil;

public class NotifyVo {

	private INotifyTriggerType triggerType;
	private String title;
	private String content;
	private List<UserVo> toUserList = new ArrayList<>();
	private List<String> toUserUuidList;
	private List<String> toTeamIdList;
	private List<String> toRoleUuidList;
	private String fromUser;
	private String fromUserEmail;
	private JSONObject data = new JSONObject();
	private String templateContent;
	private String templateTitle;

	private List<String> exceptionNotifyUserUuidList;
	private List<UserVo> exceptionNotifyUserList = new ArrayList<>();
	private StringBuilder errorBuilder;
	
	private NotifyVo(Builder builder) {
//		this.templateTitle = builder.templateTitle;
//		this.templateContent = builder.templateContent;
		this.triggerType = builder.triggerType;
		this.data = builder.data;
		this.toUserUuidList = builder.toUserUuidList;
		this.toTeamIdList = builder.toTeamIdList;
		this.toRoleUuidList = builder.toRoleUuidList;
		this.exceptionNotifyUserUuidList = builder.exceptionNotifyUserUuidList;
		try {
			title = FreemarkerUtil.transform(builder.data, builder.templateTitle);
		} catch (Exception e) {
			this.appendError(e.getMessage());
		}
		try {
			content = FreemarkerUtil.transform(builder.data, builder.templateContent);
		} catch (Exception e) {
			this.appendError(e.getMessage());
		}
	}

	private NotifyVo() {

	}

	public void addUser(UserVo userVo) {
		if (!toUserList.contains(userVo)) {
			toUserList.add(userVo);
		}
	}

	public String getTitle() {
//		if (StringUtils.isBlank(title) && StringUtils.isNotBlank(this.getTemplateTitle())) {
//			try {
//				title = FreemarkerUtil.transform(this.getData(), this.getTemplateTitle());
//			} catch (Exception e) {
//			}
//		}
		return title;
	}

	public String getContent() {
//		if (StringUtils.isBlank(content) && StringUtils.isNotBlank(this.getTemplateContent())) {
//			try {
//				content = FreemarkerUtil.transform(this.getData(), this.getTemplateContent());
//			} catch (Exception e) {
//			}
//		}
		return content;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getTemplateContent() {
		return templateContent;
	}

	public String getTemplateTitle() {
		return templateTitle;
	}

	public List<UserVo> getToUserList() {
		return toUserList;
	}

	public void setToUserList(List<UserVo> toUserList) {
		this.toUserList = toUserList;
	}

	public List<String> getToUserUuidList() {
		return toUserUuidList;
	}

	public JSONObject getData() {
		return data;
	}

	public String getFromUserEmail() {
		return fromUserEmail;
	}

	public void setFromUserEmail(String fromUserEmail) {
		this.fromUserEmail = fromUserEmail;
	}

	public List<String> getToTeamIdList() {
		return toTeamIdList;
	}

	public List<String> getToRoleUuidList() {
		return toRoleUuidList;
	}

	public List<String> getExceptionNotifyUserUuidList() {
		return exceptionNotifyUserUuidList;
	}

	public List<UserVo> getExceptionNotifyUserList() {
		return exceptionNotifyUserList;
	}

	public void setExceptionNotifyUserList(List<UserVo> exceptionNotifyUserList) {
		this.exceptionNotifyUserList = exceptionNotifyUserList;
	}

	public String getError() {
		if(errorBuilder != null) {
			return errorBuilder.toString().trim();
		}
		return null;
	}
	
	public void appendError(String errorInfo) {
		if(StringUtils.isNotBlank(errorInfo)) {
			if(errorBuilder == null) {
				errorBuilder = new StringBuilder();
			}
			errorBuilder.append(errorInfo);
		}
	}
	
	public INotifyTriggerType getTriggerType() {
		return this.triggerType;
	}

	public static class Builder {

		// 可选参数
		private String templateContent;
		private String templateTitle;
		private JSONObject data = new JSONObject();
		private List<String> toUserUuidList = new ArrayList<>();
		private List<String> toTeamIdList = new ArrayList<>();
		private List<String> toRoleUuidList = new ArrayList<>();
		private List<String> exceptionNotifyUserUuidList = new ArrayList<>();
		
		private INotifyTriggerType triggerType;
		
		public Builder(INotifyTriggerType _triggerType) {
			this.triggerType = _triggerType;
		}
		
		public INotifyTriggerType getTriggerType() {
			return triggerType;
		}

		public Builder withContentTemplate(String contentTemplate) {
			templateContent = contentTemplate;
			return this;
		}

		public Builder withTitleTemplate(String titleTemplate) {
			templateTitle = titleTemplate;
			return this;
		}

		public Builder addData(String key, Object value) {
			data.put(key, value);
			return this;
		}
		
		public Builder addAllData(JSONObject dataObj) {
			data.putAll(dataObj);
			return this;
		}
		
		public NotifyVo build() {
			return new NotifyVo(this);
		}

		public Builder addUserUuid(String userUuid) {
			if (!toUserUuidList.contains(userUuid)) {
				toUserUuidList.add(userUuid);
			}
			return this;
		}

		public Builder addTeamId(String teamId) {
			if (!toTeamIdList.contains(teamId)) {
				toTeamIdList.add(teamId);
			}
			return this;
		}

		public Builder addRoleUuid(String roleUuid) {
			if (!toRoleUuidList.contains(roleUuid)) {
				toRoleUuidList.add(roleUuid);
			}
			return this;
		}

		public Builder setExceptionNotifyUserUuidList(List<String> exceptionNotifyUserUuidList) {
			this.exceptionNotifyUserUuidList = exceptionNotifyUserUuidList;
			return this;
		}
	}
}

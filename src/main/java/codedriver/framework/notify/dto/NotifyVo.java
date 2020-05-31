package codedriver.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dto.UserVo;
import codedriver.framework.util.FreemarkerUtil;

public class NotifyVo {
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

	private NotifyVo(Builder builder) {
		this.templateTitle = builder.templateTitle;
		this.templateContent = builder.templateContent;
		this.data = builder.data;
		this.toUserUuidList = builder.toUserUuidList;
		this.toTeamIdList = builder.toTeamIdList;
		this.toRoleUuidList = builder.toRoleUuidList;
	}

	private NotifyVo() {

	}

	public void addUser(UserVo userVo) {
		if (!toUserList.contains(userVo)) {
			toUserList.add(userVo);
		}
	}

	public String getTitle() {
		if (StringUtils.isBlank(title) && StringUtils.isNotBlank(this.getTemplateTitle())) {
			try {
				title = FreemarkerUtil.transform(this.getData(), this.getTemplateTitle());
			} catch (Exception e) {
			}
		}
		return title;
	}

	public String getContent() {
		if (StringUtils.isBlank(content) && StringUtils.isNotBlank(this.getTemplateContent())) {
			try {
				content = FreemarkerUtil.transform(this.getData(), this.getTemplateContent());
			} catch (Exception e) {
			}
		}
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

	public static class Builder {

		// 可选参数
		private String templateContent;
		private String templateTitle;
		private JSONObject data = new JSONObject();
		private List<String> toUserUuidList = new ArrayList<>();
		private List<String> toTeamIdList = new ArrayList<>();
		private List<String> toRoleUuidList = new ArrayList<>();

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
	}
}

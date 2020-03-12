package codedriver.framework.dashboard.dto;

import org.apache.commons.lang3.StringUtils;

public class DashboardRoleVo {
	public enum ActionType {
		READ("read", "只读"), WRITE("write", "编辑"), SHARE("share", "共享"), DELETE("delete", "删除");

		private String value;
		private String text;

		ActionType(String _value, String _text) {
			value = _value;
			text = _text;
		}

		public String getValue() {
			return value;
		}

		public String getText() {
			return text;
		}

		public static String getText(String value) {
			for (ActionType s : ActionType.values()) {
				if (s.getValue().equals(value)) {
					return s.getText();
				}
			}
			return "";
		}
	}

	private String dashboardUuid;
	private String userId;
	private String teamUuid;
	private String roleName;
	private String action;
	private String actionText;

	public String getDashboardUuid() {
		return dashboardUuid;
	}

	public void setDashboardUuid(String dashboardUuid) {
		this.dashboardUuid = dashboardUuid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionText() {
		if (StringUtils.isBlank(actionText) && StringUtils.isNotBlank(action)) {
			actionText = ActionType.getText(action);
		}
		return actionText;
	}

	public void setActionText(String actionText) {
		this.actionText = actionText;
	}

}

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

	public DashboardRoleVo() {

	}

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

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof DashboardRoleVo))
			return false;

		final DashboardRoleVo role = (DashboardRoleVo) other;
		try {
			if (getDashboardUuid().equals(role.getDashboardUuid()) && getAction().equals(role.getAction())) {
				if (StringUtils.isNotBlank(getUserId()) && getUserId().equals(role.getUserId())) {
					return true;
				} else if (StringUtils.isNotBlank(getTeamUuid()) && getTeamUuid().equals(role.getTeamUuid())) {
					return true;
				} else if (StringUtils.isNotBlank(getRoleName()) && getRoleName().equals(role.getRoleName())) {
					return true;
				}
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = getDashboardUuid().hashCode() * 7 + getAction().hashCode() * 11;
		if (StringUtils.isNotBlank(getUserId())) {
			result += getUserId().hashCode() * 13;
		} else if (StringUtils.isNotBlank(getTeamUuid())) {
			result += getTeamUuid().hashCode() * 13;
		} else if (StringUtils.isNotBlank(getRoleName())) {
			result += getRoleName().hashCode() * 13;
		}
		return result;
	}
}

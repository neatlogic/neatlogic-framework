package codedriver.framework.common.constvalue;

public enum UserProfileOperate{
	KEEP_ON_CREATE("keeponcreate","继续创建"),
	EDIT_USER("edituser","编辑用户"),
	BACK_USER_LIST("backuserlist","返回用户列表"),
	EDIT_ROLE("editrole","编辑角色"),
	BACK_ROLE_LIST("backrolelist","返回角色列表"),
	EDIT_TEAM("editteam","编辑用户组"),
	BACK_TEAM_LIST("backteamlist","返回用户组列表");
	
	private String value;
	private String text;
	
	private UserProfileOperate(String _value,String _text){
		this.value = _value;
		this.text = _text;
	}

	public String getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

}

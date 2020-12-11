package codedriver.framework.common.constvalue;

import codedriver.framework.dto.UserTypeVo;

import java.util.HashMap;
import java.util.Map;

public enum UserType implements IUserType{
	ALL("alluser","所有人",true),LOGIN_USER("loginuser","当前登录人",false),LOGIN_TEAM("loginteam","当前登录人所在组",false),LOGIN_DEPARTMENT("logindepartment","当前登录人所在部",false),VIP_USER("vipuser","vip用户",false);

	private String status;
	private String text;
	private Boolean isDefultShow;

	private UserType(String _status, String _text,Boolean _isDefultShow) {
		this.status = _status;
		this.text = _text;
		this.isDefultShow = _isDefultShow;
	}

	public String getValue() {
		return status;
	}

	public String getText() {
		return text;
	}

	public Boolean getIsDefultShow() {
		return isDefultShow;
	}

	public static String getValue(String _status) {
		for (UserType s : UserType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getValue();
			}
		}
		return null;
	}

	public static String getText(String _status) {
		for (UserType s : UserType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getText();
			}
		}
		return "";
	}


	@Override
	public UserTypeVo getUserType() {
		UserTypeVo vo = new UserTypeVo();
		vo.setModuleId(getModuleId());
		Map<String,String> map = new HashMap<>();
		for(UserType type : UserType.values()){
			map.put(type.getValue(),type.getText());
		}
		vo.setValues(map);
		return vo;
	}

	@Override
	public String getModuleId() {
		return "framework";
	}
}

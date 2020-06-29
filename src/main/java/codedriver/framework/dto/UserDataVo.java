package codedriver.framework.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class UserDataVo implements Cloneable{
	@EntityField(name = "用户uuid",
			type = ApiParamType.STRING)
	private String userUuid;
	@EntityField(name = "数据",
			type = ApiParamType.STRING)
	private String data;
	@EntityField(name = "功能类型",
			type = ApiParamType.STRING)
	private String type;


	public UserDataVo() {

	}

	public UserDataVo(String userUuid, String data, String type) {
		this.userUuid = userUuid;
		this.data = data;
		this.type = type;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public String getData() {
		return data;
	}

	public String getType() {
		return type;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object clone() throws CloneNotSupportedException{
	    return super.clone();
	}

}

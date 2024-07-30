package neatlogic.framework.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

public class UserDataVo implements Cloneable{
	@EntityField(name = "common.useruuid", type = ApiParamType.STRING)
	private String userUuid;
	@EntityField(name = "common.data", type = ApiParamType.JSONOBJECT)
	private JSONObject data;
	@EntityField(name = "common.type", type = ApiParamType.STRING)
	private String type;

	@JSONField(serialize = false)
	private String dataStr;

	public UserDataVo() {

	}

	public UserDataVo(String userUuid, JSONObject data, String type) {
		this.userUuid = userUuid;
		this.data = data;
		this.type = type;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public JSONObject getData() {
		if (data == null && dataStr != null) {
			data = JSONObject.parseObject(dataStr);
		}
		return data;
	}

	public String getType() {
		return type;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDataStr() {
		if (dataStr == null && data != null) {
			dataStr = data.toJSONString();
		}
		return dataStr;
	}

	public void setDataStr(String dataStr) {
		this.dataStr = dataStr;
	}

	public Object clone() throws CloneNotSupportedException{
	    return super.clone();
	}

}

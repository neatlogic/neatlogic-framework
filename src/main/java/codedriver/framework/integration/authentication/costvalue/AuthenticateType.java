package codedriver.framework.integration.authentication.costvalue;

import codedriver.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public enum AuthenticateType implements IEnum {
	NOAUTH("noauth", "无需认证"), BUILDIN("buildin", "内部验证"), BASIC("basicauth", "Basic认证"), BEARER("bearertoken", "Bearer Token");

	private String type;
	private String text;

	private AuthenticateType(String _type, String _text) {
		this.type = _type;
		this.text = _text;
	}

	public String getValue() {
		return this.type;
	}

	public String getText() {
		return this.text;
	}


	@Override
	public List getValueTextList() {
		JSONArray array = new JSONArray();
		for(AuthenticateType type : AuthenticateType.values()){
			array.add(new JSONObject(){
				{
					this.put("value",type.getValue());
					this.put("text",type.getText());
				}
			});
		}
		return array;
	}
}

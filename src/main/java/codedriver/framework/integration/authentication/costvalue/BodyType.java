package codedriver.framework.integration.authentication.costvalue;

import codedriver.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;


public enum BodyType implements IEnum {
	RAW("raw"), X_WWW_FORM_URLENCODED("x-www-form-urlencoded");

	private String type;

	private BodyType(String _type) {
		this.type = _type;
	}

	public String toString() {
		return this.type;
	}


	@Override
	public List getValueTextList() {
		JSONArray array = new JSONArray();
		for(BodyType type : BodyType.values()){
			array.add(new JSONObject(){
				{
					this.put("value",type.toString());
					this.put("text",type.toString());
				}
			});
		}
		return array;
	}
}

package codedriver.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;


public enum GroupSearch implements IEnum{
	USER("user", "用户类型"),
	TEAM("team", "组类型"),
	ROLE("role", "角色类型"),
	COMMON("common", "公共类型"),
	;
	
	private String value;
	private String text;
	private GroupSearch(String value, String text) {
		this.value = value;
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	
	public String getValuePlugin() {
		return value + "#";
	}

	public String getText() {
		return text;
	}
	
	public static String getValue(String _value) {
		for(GroupSearch gs : GroupSearch.values()) {
			if(gs.value.equals(_value)) {
				return gs.value;
			}
		}
		return null;
	}
	
	public static GroupSearch getGroupSearch(String _value) {
		for(GroupSearch gs : GroupSearch.values()) {
			if(gs.value.equals(_value)) {
				return gs;
			}
		}
		return null;
	}
	
	public static String removePrefix(String _value) {
		for(GroupSearch gs : GroupSearch.values()) {
			if(_value.startsWith(gs.getValuePlugin())) {
				return _value.substring(gs.getValuePlugin().length());
			}
		}
		return _value;
	}


	@Override
	public List getValueTextList() {
		JSONArray array = new JSONArray();
		for(GroupSearch groupSearch : GroupSearch.values()){
			array.add(new JSONObject(){
				{
					this.put("value",groupSearch.getValue());
					this.put("text",groupSearch.getText());
				}
			});
		}
		return array;
	}
}

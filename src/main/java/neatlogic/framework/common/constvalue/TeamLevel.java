package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public enum TeamLevel implements IEnum{

	GROUP("group", "集团", 1),
	COMPANY("company", "公司", 2),
	CENTER("center", "中心", 3),
	DEPARTMENT("department", "部门", 4),
	TEAM("team", "组", 5);
	private String value;
	private String text;
	private int level;
	private TeamLevel(String value, String text, int level) {
		this.value = value;
		this.text = text;
		this.level = level;
	}
	public String getValue() {
		return value;
	}
	public String getText() {
		return text;
	}
	public int getLevel() {
		return level;
	}
	public static String getValue(String _value) {
		for(TeamLevel type : values()) {
			if(type.getValue().equals(_value)) {
				return type.getValue();
			}
		}
		return null;
	}


	@Override
	public List getValueTextList() {
		JSONArray array = new JSONArray();
		for(TeamLevel level : TeamLevel.values()){
			array.add(new JSONObject(){
				{
					this.put("value",level.getValue());
					this.put("text",level.getText());
				}
			});
		}
		return array;
	}
}

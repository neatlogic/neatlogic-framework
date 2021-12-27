package codedriver.framework.common.constvalue;

import codedriver.framework.common.config.Config;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Objects;

public enum DeviceType implements IEnum{
	ALL("all","所有"),MOBILE("mobile","手机端"),PC("pc","电脑端");

	private String status;
	private String text;

	private DeviceType(String _status, String _text) {
		this.status = _status;
		this.text = _text;
	}

	public String getValue() {
		return status;
	}

	public String getText() {
		return text;
	}

	public static String getValue(String _status) {
		for (DeviceType s : DeviceType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getValue();
			}
		}
		return null;
	}

	public static String getText(String _status) {
		for (DeviceType s : DeviceType.values()) {
			if (s.getValue().equals(_status)) {
				return s.getText();
			}
		}
		return "";
	}

	@Override
	public List getValueTextList() {
		JSONArray array = new JSONArray();
		for (DeviceType type : values()) {
			if(!Config.MOBILE_IS_ONLINE() && Objects.equals(type.getValue(),DeviceType.MOBILE.getValue())){
				continue;
			}
			array.add(new JSONObject() {
				{
					this.put("value", type.getValue());
					this.put("text", type.getText());
				}
			});
		}
		return array;
	}
}

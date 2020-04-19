package codedriver.framework.integration.dto;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class PartternVo {
	public enum Type {
		STRING("string", "字符"), NUMBER("number", "数字"), OBJECT("object", "对象"), OBJLIST("objlist", "对象数组"), STRLIST("strlist", "字符数组"), NUMLIST("numlist", "数字数组");

		private String status;
		private String text;

		private Type(String _status, String _text) {
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
			for (Type s : Type.values()) {
				if (s.getValue().equals(_status)) {
					return s.getValue();
				}
			}
			return null;
		}

		public static String getText(String name) {
			for (Type s : Type.values()) {
				if (s.getValue().equals(name)) {
					return s.getText();
				}
			}
			return "";
		}
	}

	@SuppressWarnings("unused")
	private PartternVo() {
	}

	public PartternVo(String _name, String _type) {
		name = _name;
		type = _type;
	}

	@EntityField(name = "参数名", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "参数类型", type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "参数类型名称", type = ApiParamType.STRING)
	private String typeName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeName() {
		if (StringUtils.isBlank(typeName) && StringUtils.isNotBlank(type)) {
			typeName = Type.getText(type);
		}
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}

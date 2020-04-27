package codedriver.framework.integration.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class PatternVo {

	@SuppressWarnings("unused")
	private PatternVo() {
	}

	public PatternVo(String _name, ApiParamType _type) {
		name = _name;
		type = _type.getValue();
	}

	@EntityField(name = "参数名", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "参数类型", type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "参数类型名称", type = ApiParamType.STRING)
	private String typeName;
	@EntityField(name = "子参数", type = ApiParamType.JSONOBJECT)
	private List<PatternVo> children;

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
			typeName = ApiParamType.getText(type);
		}
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public List<PatternVo> getChildren() {
		return children;
	}

	public void setChildren(List<PatternVo> childPatternList) {
		this.children = childPatternList;
	}

	public void addChild(PatternVo patternVo) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.add(patternVo);
	}

}

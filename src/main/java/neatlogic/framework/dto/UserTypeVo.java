package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.Map;

public class UserTypeVo{

	@EntityField(name = "模块id", type = ApiParamType.STRING)
	private String moduleId;
	@EntityField(name = "枚举值", type = ApiParamType.JSONARRAY)
	private Map<String,String> values;

	public UserTypeVo(){};

	public UserTypeVo(String moduleId, Map<String, String> values) {
		this.moduleId = moduleId;
		this.values = values;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}
}

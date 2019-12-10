package codedriver.framework.file.dto;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class FileTypeVo {
	@EntityField(name = "名称",
			type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "中文名称",
			type = ApiParamType.STRING)
	private String displayName;
	@EntityField(name = "附件归属",
			type = ApiParamType.STRING)
	private String belong;
	private String moduleId;
	private String config;
	private JSONObject configObj;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public JSONObject getConfigObj() {
		if (configObj == null && StringUtils.isNotBlank(config)) {
			configObj = JSONObject.parseObject(config);
		}
		return configObj;
	}

	public void setConfigObj(JSONObject configObj) {
		this.configObj = configObj;
	}

	public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}
}

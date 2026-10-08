package neatlogic.framework.file.dto;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.file.core.IFileTypeHandler;

public class FileTypeVo {
	@EntityField(name = "名称",
			type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "中文名称",
			type = ApiParamType.STRING)
	private String displayName;
	private String moduleId;
	private String config;
	private JSONObject configObj;
	private transient IFileTypeHandler handler;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** 返回当前语言环境下的文件类型显示名称。 */
	public String getDisplayName() {
		if (handler != null) {
			return handler.getDisplayName();
		}
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/** 绑定文件类型处理器，使显示名称可按当前请求语言动态解析。 */
	public void setHandler(IFileTypeHandler handler) {
		this.handler = handler;
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

}

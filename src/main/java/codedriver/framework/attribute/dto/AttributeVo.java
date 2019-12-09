package codedriver.framework.attribute.dto;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.attribute.constvalue.AttributeHandler;
import codedriver.framework.attribute.constvalue.AttributeType;
import codedriver.framework.attribute.core.AttributeHandlerFactory;
import codedriver.framework.attribute.core.IAttributeHandler;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class AttributeVo extends BasePageVo implements Serializable {
	private static final long serialVersionUID = 7749177698131954822L;
	@EntityField(name = "属性uuid",
			type = ApiParamType.STRING)
	private String uuid;
	private transient String keyword;
	@EntityField(name = "属性英文名称",
			type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "属性中文名称",
			type = ApiParamType.STRING)
	private String label;
	@EntityField(name = "属性处理器英文名称",
			type = ApiParamType.STRING)
	private String handler;
	@EntityField(name = "属性处理器中文名称",
			type = ApiParamType.STRING)
	private String handlerName;
	@EntityField(name = "属性类型英文名称",
			type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "属性类型中文名称",
			type = ApiParamType.STRING)
	private String typeName;
	private String unit;
	private String config;
	private JSONObject configObj;
	private String description;
	private String dataCubeTextField;
	private String dataCubeValueField;
	private String dataCubeUuid;
	private Integer isActive;
	private String inputPage;
	private String viewPage;
	private String configPage;
	private String editTemplate;
	private String viewTemplate;
	private String configTemplate;

	public AttributeVo() {
		this.setPageSize(20);
	}

	public String getUuid() {
		if (StringUtils.isBlank(uuid)) {
			uuid = UUID.randomUUID().toString().replace("-", "");
		}
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		if (keyword != null) {
			keyword = keyword.trim();
		}
		this.keyword = keyword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null) {
			name = name.trim();
		}
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		if (label != null) {
			label = label.trim();
		}
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeName() {
		if (StringUtils.isNotBlank(type) && StringUtils.isBlank(typeName)) {
			typeName = AttributeType.getName(type);
		}
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public JSONObject getConfigObj() {
		if (configObj == null && StringUtils.isNotBlank(config)) {
			try {
				configObj = JSONObject.parseObject(config);
			} catch (Exception ex) {
			}
		}
		return configObj;
	}

	public void setConfigObj(JSONObject configObj) {
		this.configObj = configObj;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getHandlerName() {
		if (StringUtils.isNotBlank(handler) && StringUtils.isBlank(handlerName)) {
			handlerName = AttributeHandler.getName(handler);
		}
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	public String getDataCubeTextField() {
		return dataCubeTextField;
	}

	public void setDataCubeTextField(String dataCubeTextField) {
		this.dataCubeTextField = dataCubeTextField;
	}

	public String getDataCubeValueField() {
		return dataCubeValueField;
	}

	public void setDataCubeValueField(String dataCubeValueField) {
		this.dataCubeValueField = dataCubeValueField;
	}

	public String getDataCubeUuid() {
		return dataCubeUuid;
	}

	public void setDataCubeUuid(String dataCubeUuid) {
		this.dataCubeUuid = dataCubeUuid;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getInputPage() {
		if (StringUtils.isBlank(inputPage) && StringUtils.isNotBlank(handler)) {
			IAttributeHandler attributeHandler = AttributeHandlerFactory.getHandler(handler);
			if (attributeHandler != null) {
				inputPage = attributeHandler.getInputPage();
			}
		}
		return inputPage;
	}

	public String getConfigPage() {
		if (StringUtils.isBlank(configPage) && StringUtils.isNotBlank(handler)) {
			IAttributeHandler attributeHandler = AttributeHandlerFactory.getHandler(handler);
			if (attributeHandler != null) {
				configPage = attributeHandler.getConfigPage();
			}
		}
		return configPage;
	}

	public void setInputPage(String inputPage) {
		this.inputPage = inputPage;
	}

	public String getViewPage() {
		if (StringUtils.isBlank(viewPage) && StringUtils.isNotBlank(handler)) {
			IAttributeHandler attributeHandler = AttributeHandlerFactory.getHandler(handler);
			if (attributeHandler != null) {
				viewPage = attributeHandler.getViewPage();
			}
		}
		return viewPage;
	}

	public void setViewPage(String viewPage) {
		this.viewPage = viewPage;
	}

	public String getEditTemplate() {
		return editTemplate;
	}

	public void setEditTemplate(String editTemplate) {
		this.editTemplate = editTemplate;
	}

	public String getViewTemplate() {
		return viewTemplate;
	}

	public void setViewTemplate(String viewTemplate) {
		this.viewTemplate = viewTemplate;
	}

	public String getConfigTemplate() {
		return configTemplate;
	}

	public void setConfigTemplate(String configTemplate) {
		this.configTemplate = configTemplate;
	}

}

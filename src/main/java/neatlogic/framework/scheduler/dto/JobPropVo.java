package neatlogic.framework.scheduler.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;

public class JobPropVo {
	
	@EntityField(name = "common.id", type = ApiParamType.STRING)
	private Long id;
	@EntityField(name = "common.jobuuid", type = ApiParamType.STRING)
	private String jobUuid;
	@EntityField(name = "common.name", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "common.value", type = ApiParamType.STRING)
	private String value;
	@EntityField(name = "common.datatype", type = ApiParamType.STRING)
	private String dataType;
	@EntityField(name = "common.description", type = ApiParamType.STRING)
	private String description;
	@EntityField(name = "common.isrequired", type = ApiParamType.BOOLEAN)
	private Boolean required;
	@EntityField(name = "common.sort", type = ApiParamType.INTEGER)
	private Integer sort;
	@EntityField(name = "common.help", type = ApiParamType.STRING)
	private String help;
	public JobPropVo() {
		
	}
		
	public JobPropVo(Long id, String name, String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public JobPropVo(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Long getId() {
		if (id == null) {
			id = SnowflakeUtil.uniqueLong();
		}
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getJobUuid() {
		return jobUuid;
	}
	public void setJobUuid(String jobUuid) {
		this.jobUuid = jobUuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}
}

package codedriver.framework.scheduler.dto;

import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class JobPropVo {
	
	@EntityField(name = "属性id", type = ApiParamType.STRING)
	private Long id;
	@JSONField(serialize = false)
	private transient String jobUuid;
	@EntityField(name = "属性名", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "属性值", type = ApiParamType.STRING)
	private String value;
	
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
}

package codedriver.framework.scheduler.dto;

public class JobPropVo {

	private Long id;
	private Long jobId;
	private String name;
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
	public Long getJobId() {
		return jobId;
	}
	public void setJobId(Long jobId) {
		this.jobId = jobId;
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

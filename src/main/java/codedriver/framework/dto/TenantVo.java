package codedriver.framework.dto;

public class TenantVo {
	
	public final static String DISABLE_UUID = "master";
	private String uuid;
	private String name;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

package codedriver.framework.dto;

public class UserProfileVo implements Cloneable{
	private String userId;
	private String moduleId;
	private String moduleName;
	private String config;
	
	public UserProfileVo() {
		
	}
	
	public UserProfileVo(String userId, String moduleId, String config) {
		this.userId = userId;
		this.moduleId = moduleId;
		this.config = config;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public Object clone() throws CloneNotSupportedException{
	    return super.clone();
	}

}

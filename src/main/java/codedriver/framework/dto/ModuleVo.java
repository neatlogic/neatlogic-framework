package codedriver.framework.dto;

public class ModuleVo {

	public static final int BALANTFLOW_MODULE_KNOWS_ID = 1;
	public static final int BALANTFLOW_MODULE_CASE_ID = 2;
	public static final int BALANTFLOW_MODULE_FLOW_ID = 3;
	public static final int BALANTFLOW_MODULE_FACE_ID = 4;
	public static final int BALANTFLOW_MODULE_PROJECT_ID = 5;
	public static final int BALANTFLOW_MODULE_REPORT_ID = 6;
	public static final int BALANTFLOW_MODULE_CMDB_ID = 7;
	public static final int BALANTFLOW_MODULE_CHANGE_ID = 8;
	public static final int BALANTFLOW_MODULE_PROBLEM_ID = 9;
	public static final int BALANTFLOW_MODULE_TASK_ID = 10;
	public static final int BALANTFLOW_MODULE_SF_CUSTOM_ID = 11;
	public static final int BALANTFLOW_MODULE_RELEASE_ID = 13;
	public static final int BALANTFLOW_MODULE_ECMDB_ID = 14;
	public static final int BALANTFLOW_MODULE_MONITOR_ID = 15;
	public static final int BALANTFLOW_MODULE_OCTOPUS_ID = 19;

//	private Long id;
	private String name;
	private String description;
	private String status;
	private String statusText;
	private Integer isActive;
	private String configPath;
//	private String initTime;
	private String error;
//	private String color;
	private String urlMapping;
//	private String icon;
//	private String versionPath;
	private String version;
	private int startup;
	private Integer widgetCount;
	private int isShow;

	private int currentPage;
	private int pageSize;
	private int startNum;


//	public String getColor() {
//		return color;
//	}
//
//	public void setColor(String color) {
//		this.color = color;
//	}

	public String getUrlMapping() {
		return urlMapping;
	}

	public void setUrlMapping(String urlMapping) {
		this.urlMapping = urlMapping;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

//	public String getIcon() {
//		return icon;
//	}
//
//	public void setIcon(String icon) {
//		this.icon = icon;
//	}
//
//	public String getInitTime() {
//		return initTime;
//	}
//
//	public void setInitTime(String initTime) {
//		this.initTime = initTime;
//	}
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusText() {
		if (this.status != null) {
			if (this.status.equals("success")) {
				statusText = "加载成功";
			} else if (this.status.equals("failed")) {
				statusText = "加载失败";
			} else {
				statusText = "未加载";
			}
		}
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public int getStartup() {
		return startup;
	}

	public void setStartup(int startup) {
		this.startup = startup;
	}

	public Integer getWidgetCount() {
		return widgetCount;
	}

	public void setWidgetCount(Integer widgetCount) {
		this.widgetCount = widgetCount;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStartNum() {
		return startNum;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public int getIsShow() {
		return isShow;
	}

	public void setIsShow(int isShow) {
		this.isShow = isShow;
	}

//	public String getVersionPath() {
//		return versionPath;
//	}
//
//	public void setVersionPath(String versionPath) {
//		this.versionPath = versionPath;
//	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}

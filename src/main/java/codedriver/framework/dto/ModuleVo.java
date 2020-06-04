package codedriver.framework.dto;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class ModuleVo {
	@EntityField(name = "模块id", type = ApiParamType.STRING)
	private String id;
	@EntityField(name = "模块名", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "模块描述", type = ApiParamType.STRING)
	private String description;
	@EntityField(name = "模块版本", type = ApiParamType.STRING)
	private String version;
	@EntityField(name = "模块分组", type = ApiParamType.STRING)
	private String group;
	@EntityField(name = "分组名称", type = ApiParamType.STRING)
	private String groupName;
	@EntityField(name = "分组描述", type = ApiParamType.STRING)
	private String groupDescription;
	@EntityField(name = "分组排序", type = ApiParamType.INTEGER)
	private int groupSort;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupSort() {
		return groupSort;
	}

	public void setGroupSort(int groupSort) {
		this.groupSort = groupSort;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	
}

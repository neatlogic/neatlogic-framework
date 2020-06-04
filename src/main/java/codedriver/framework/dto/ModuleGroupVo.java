package codedriver.framework.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

public class ModuleGroupVo {
	
	@EntityField(name = "模块分组", type = ApiParamType.STRING)
	private String group;
	@EntityField(name = "模块分组名称", type = ApiParamType.STRING)
	private String groupName;
	@EntityField(name = "模块分组排序", type = ApiParamType.STRING)
	private Integer groupSort;
	@EntityField(name = "模块分组描述", type = ApiParamType.INTEGER)
	private String groupDescription;
	@EntityField(name = "模块分组对应的moduleVo列表", type = ApiParamType.STRING)
	private List<ModuleVo> moduleList;
	
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

	public List<ModuleVo> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<ModuleVo> moduleList) {
		this.moduleList = moduleList;
	}
	
	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public Integer getGroupSort() {
		return groupSort;
	}

	public void setGroupSort(Integer groupSort) {
		this.groupSort = groupSort;
	}

	public List<String> getModuleIdList() {
		List<String> moduleIdList = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(this.moduleList)) {
			for(ModuleVo moduleVo : this.moduleList) {
				moduleIdList.add(moduleVo.getId());
			}
		}
		return moduleIdList;
	}

}

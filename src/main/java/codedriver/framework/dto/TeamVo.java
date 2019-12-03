package codedriver.framework.dto;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.dto.BasePageVo;

public class TeamVo extends BasePageVo{
	
	private String uuid;
	private Integer lft;
	private Integer rht;
	private Integer isShow;
	private Long timeId;
	private String parentId;	
	private String importId;
	private String name;
	private String description;
	private String roleName;
	private String module;
	private String parentName;
	private int isHandleChildtask;
	private int isDelete ;
	private int workTimeId;
	private int sort;
	private int childCount;
	private Integer componentId;
	private List<String> moduleList;

	public synchronized String getUuid() {
		  if (StringUtils.isBlank(uuid)) {
		   uuid = UUID.randomUUID().toString().replace("-", "");
		  }
		  return uuid;
		 }

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getIsShow() {
		return isShow;
	}

	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}

	public String getImportId() {
		return importId;
	}

	public void setImportId(String importId) {
		this.importId = importId;
	}	

	public TeamVo() {

	}

	public TeamVo(int left, int right) {
		this.lft = left;
		this.rht = right;
	}

	public Integer getLft() {
		return lft;
	}

	public void setLft(Integer lft) {
		this.lft = lft;
	}

	public Integer getRht() {
		return rht;
	}

	public void setRht(Integer rht) {
		this.rht = rht;
	}

	public Long getTimeId() {
		return timeId;
	}

	public void setTimeId(Long timeId) {
		this.timeId = timeId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

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

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public int getIsHandleChildtask() {
		return isHandleChildtask;
	}

	public void setIsHandleChildtask(int isHandleChildtask) {
		this.isHandleChildtask = isHandleChildtask;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public int getWorkTimeId() {
		return workTimeId;
	}

	public void setWorkTimeId(int workTimeId) {
		this.workTimeId = workTimeId;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getChildCount() {
		return childCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	public Integer getComponentId() {
		return componentId;
	}

	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
	}

	public List<String> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<String> moduleList) {
		this.moduleList = moduleList;
	}
	
}

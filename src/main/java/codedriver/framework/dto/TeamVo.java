package codedriver.framework.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class TeamVo extends BasePageVo {
	
	public static final String ROOT_PARENTUUID = "-1";
	public static final String ROOT_UUID = "0";
	
	private transient String keyword;
	private transient Boolean isAutoGenerateUuid = true;
	
	@EntityField(name = "分组uuid", type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "分组名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "父分组uuid", type = ApiParamType.STRING)
	private String parentUuid;
	@EntityField(name = "子节点数量", type = ApiParamType.INTEGER)
	private Integer childCount;
	
	@EntityField(name = "左编码", type = ApiParamType.INTEGER)
	private Integer lft;
	@EntityField(name = "右编码", type = ApiParamType.INTEGER)
	private Integer rht;

	private int userCount;

	private List<String> pathNameList;
	@EntityField(name = "级别", type = ApiParamType.STRING)
	private String level;
	
	public List<String> getPathNameList() {
		return pathNameList;
	}

	public void setPathNameList(List<String> pathNameList) {
		this.pathNameList = pathNameList;
	}

	public synchronized String getUuid() {
		if (StringUtils.isBlank(uuid) && isAutoGenerateUuid) {
			uuid = UUID.randomUUID().toString().replace("-", "");
		}
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public TeamVo() {

	}

	public TeamVo(Boolean _isAutoGenerateUuid) {
		this.isAutoGenerateUuid = _isAutoGenerateUuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	public Integer getChildCount() {
		return childCount;
	}

	public void setChildCount(Integer childCount) {
		this.childCount = childCount;
	}

	public Boolean getIsAutoGenerateUuid() {
		return isAutoGenerateUuid;
	}

	public void setIsAutoGenerateUuid(Boolean isAutoGenerateUuid) {
		this.isAutoGenerateUuid = isAutoGenerateUuid;
	}

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
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
	
	private transient TeamVo parent;
	
	private List<TeamVo> children = new ArrayList<>();

	public TeamVo getParent() {
		return parent;
	}

	public void setParent(TeamVo parent) {
		this.parent = parent;
		parent.getChildren().add(this);
	}

	public List<TeamVo> getChildren() {
		return children;
	}

	public void setChildren(List<TeamVo> children) {
		this.children = children;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}

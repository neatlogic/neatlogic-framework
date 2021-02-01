package codedriver.framework.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import codedriver.framework.common.constvalue.GroupSearch;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class TeamVo extends BasePageVo {
	
	public static final String ROOT_PARENTUUID = "-1";
	public static final String ROOT_UUID = "0";
	@JSONField(serialize=false)
	private transient String keyword;
	@JSONField(serialize=false)
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

	@EntityField(name = "父分组名称", type = ApiParamType.STRING)
	@JSONField(serialize = false)
	private transient String parentName;

	@EntityField(name = "分组完整路径", type = ApiParamType.STRING)
	private String fullPath;

	/**
	 * 此字段专供前端使用，用于渲染头像时区分对象类型，取值范围[user,team,role]
	 */
	@EntityField(name = "前端初始化类型，取值范围[user,team,role]", type = ApiParamType.STRING)
	private final String initType = GroupSearch.TEAM.getValue();
	
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
	@JSONField(serialize=false)
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

	public String getInitType() {
		return initType;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
}

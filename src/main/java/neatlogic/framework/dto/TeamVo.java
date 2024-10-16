package neatlogic.framework.dto;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamVo extends BaseEditorVo implements Serializable {
	
	public static final String ROOT_PARENTUUID = "-1";
	public static final String ROOT_UUID = "0";
	private static final long serialVersionUID = -114822145668874331L;
	@JSONField(serialize=false)
	private Boolean isAutoGenerateUuid = true;
	private Long id;
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
	@EntityField(name = "是否已删除", type = ApiParamType.INTEGER)
	private Integer isDelete;

	@EntityField(name = "父分组名称", type = ApiParamType.STRING)
	@JSONField(serialize = false)
	private String parentName;

	@EntityField(name = "分组完整路径", type = ApiParamType.STRING)
	private String fullPath;

	@EntityField(name = "数据來源", type = ApiParamType.STRING)
	private String source;

	@EntityField(name = "父分组路径List", type = ApiParamType.JSONARRAY)
	private List<String> parentPathList;

	@JSONField(serialize = false)
	private Integer nameRepeatCount;//重名的分组数量

	/**
	 * 此字段专供前端使用，用于渲染头像时区分对象类型，取值范围[user,team,role]
	 */
	@EntityField(name = "前端初始化类型，取值范围[user,team,role]", type = ApiParamType.STRING)
	private final String initType = GroupSearch.TEAM.getValue();

	@EntityField(name = "分组领导", type = ApiParamType.JSONARRAY)
	private List<TeamUserTitleVo> teamUserTitleList;

	private Integer checked;
	private Integer checkedChildren;
	@JSONField(serialize = false)
	private String upwardUuidPath;
	@EntityField(name = "名称路径", type = ApiParamType.STRING)
	private String upwardNamePath;
	@JSONField(serialize = false)
	private List<String> parentTeamUuidList;
	@JSONField(serialize = false)
	private List<String> rangeList;
	@JSONField(serialize = false)
	private List<String> teamUuidList;
	@EntityField(name = "子分组uuid", type = ApiParamType.STRING)
	private String childrenTeamUuid;
	@EntityField(name = "子分组name", type = ApiParamType.STRING)
	private String childrenTeamName;

	public TeamVo(String uuid) {
		this.uuid = uuid;
	}

	public List<String> getPathNameList() {
		return pathNameList;
	}

	public void setPathNameList(List<String> pathNameList) {
		this.pathNameList = pathNameList;
	}

	public Long getId() {
		if (id == null) {
			id = SnowflakeUtil.uniqueLong();
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	private TeamVo parent;
	
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

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getNameRepeatCount() {
		return nameRepeatCount;
	}

	public void setNameRepeatCount(Integer nameRepeatCount) {
		this.nameRepeatCount = nameRepeatCount;
	}

	public List<TeamUserTitleVo> getTeamUserTitleList() {
		return teamUserTitleList;
	}

	public void setTeamUserTitleList(List<TeamUserTitleVo> teamUserTitleList) {
		this.teamUserTitleList = teamUserTitleList;
	}

	public List<String> getParentPathList() {
		return parentPathList;
	}

	public void setParentPathList(List<String> parentPathList) {
		this.parentPathList = parentPathList;
	}

	public Integer getChecked() {
		return checked;
	}

	public void setChecked(Integer checked) {
		this.checked = checked;
	}

	public Integer getCheckedChildren() {
		return checkedChildren;
	}

	public void setCheckedChildren(Integer checkedChildren) {
		this.checkedChildren = checkedChildren;
	}

	public String getUpwardUuidPath() {
		return upwardUuidPath;
	}

	public void setUpwardUuidPath(String upwardUuidPath) {
		this.upwardUuidPath = upwardUuidPath;
	}

	public String getUpwardNamePath() {
		return upwardNamePath;
	}

	public void setUpwardNamePath(String upwardNamePath) {
		this.upwardNamePath = upwardNamePath;
	}

	public List<String> getParentTeamUuidList() {
		return parentTeamUuidList;
	}

	public void setParentTeamUuidList(List<String> parentTeamUuidList) {
		this.parentTeamUuidList = parentTeamUuidList;
	}

	public List<String> getRangeList() {
		return rangeList;
	}

	public void setRangeList(List<String> rangeList) {
		this.rangeList = rangeList;
	}

	public List<String> getTeamUuidList() {
		return teamUuidList;
	}

	public void setTeamUuidList(List<String> teamUuidList) {
		this.teamUuidList = teamUuidList;
	}

	public String getChildrenTeamUuid() {
		return childrenTeamUuid;
	}

	public void setChildrenTeamUuid(String childrenTeamUuid) {
		this.childrenTeamUuid = childrenTeamUuid;
	}

	public String getChildrenTeamName() {
		return childrenTeamName;
	}

	public void setChildrenTeamName(String childrenTeamName) {
		this.childrenTeamName = childrenTeamName;
	}
}

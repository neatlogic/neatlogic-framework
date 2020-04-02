package codedriver.framework.dto;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

public class TeamVo extends BasePageVo {
	private transient String keyword;
	private transient Boolean isAutoGenerateUuid = true;
	@EntityField(name = "分组uuid",
			type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "分组名称",
			type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "父分组uuid",
			type = ApiParamType.STRING)
	private String parentUuid;
	@EntityField(name = "子节点数量",
			type = ApiParamType.INTEGER)
	private Integer childCount;
	@EntityField(name = "排序",
			type = ApiParamType.INTEGER)
	private Integer sort;

	@EntityField(name = "标签集合", type = ApiParamType.JSONARRAY)
	private List<TagVo> tagList;

	private Long tagId;

	private int userCount;

	private List<String> userIdList;

	private List<String> pathNameList;

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

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
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

	public List<TagVo> getTagList() {
		return tagList;
	}

	public void setTagList(List<TagVo> tagList) {
		this.tagList = tagList;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public List<String> getUserIdList() {
		return userIdList;
	}

	public void setUserIdList(List<String> userIdList) {
		this.userIdList = userIdList;
	}
}

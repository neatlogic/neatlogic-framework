package codedriver.framework.dashboard.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.annotation.JSONField;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.dto.AuthorityVo;
import codedriver.framework.restful.annotation.EntityField;

public class DashboardVo extends BasePageVo {
	private transient String keyword;
	@EntityField(name = "仪表板uuid", type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "仪表板名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "是否激活", type = ApiParamType.INTEGER)
	private int isActive;
	@EntityField(name = "是否默认面板", type = ApiParamType.INTEGER)
	private int isDefault;
	@EntityField(name = "描述", type = ApiParamType.STRING)
	private String description;
	@EntityField(name = "仪表板创建时间", type = ApiParamType.LONG)
	private Date fcd;
	@EntityField(name = "仪表板创建人", type = ApiParamType.STRING)
	private String fcu;
	@EntityField(name = "仪表板创建人名", type = ApiParamType.STRING)
	private String fcuName;
	@EntityField(name = "仪表板修改时间", type = ApiParamType.LONG)
	private Date lcd;
	@EntityField(name = "仪表板修改人", type = ApiParamType.STRING)
	private String lcu;
	@EntityField(name = "仪表板修改人名", type = ApiParamType.STRING)
	private String lcuName;
	@EntityField(name = "仪表板组件列表", type = ApiParamType.JSONOBJECT)
	private List<DashboardWidgetVo> widgetList;
	@EntityField(name = "system：系统分类  custom：自定义分类", type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "授权列表", type = ApiParamType.STRING)
	private List<String> valueList;
	@EntityField(name = "默认用户", type = ApiParamType.STRING)
	private String defaultUser;
	@EntityField(name = "默认用户", type = ApiParamType.STRING)
	private String defaultType;
	@EntityField(name = "是否拥有编辑权限", type = ApiParamType.JSONARRAY)
	private Integer isCanEdit;
	@EntityField(name = "是否拥有授权权限", type = ApiParamType.JSONARRAY)
	private Integer isCanRole;
	@JSONField(serialize = false)
	private List<AuthorityVo> authorityList;
	
	//params
	private String userId;
	private List<String> teamUuidList;
	private List<String> roleNameList;
	
	public String getUuid() {
		if (StringUtils.isBlank(uuid)) {
			uuid = UUID.randomUUID().toString().replace("-", "");
		}
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

	public Date getFcd() {
		return fcd;
	}

	public void setFcd(Date fcd) {
		this.fcd = fcd;
	}

	public String getFcu() {
		return fcu;
	}

	public void setFcu(String fcu) {
		this.fcu = fcu;
	}

	public Date getLcd() {
		return lcd;
	}

	public void setLcd(Date lcd) {
		this.lcd = lcd;
	}

	public String getLcu() {
		return lcu;
	}

	public void setLcu(String lcu) {
		this.lcu = lcu;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public List<DashboardWidgetVo> getWidgetList() {
		return widgetList;
	}

	public void setWidgetList(List<DashboardWidgetVo> widgetList) {
		this.widgetList = widgetList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

	public String getFcuName() {
		return fcuName;
	}

	public void setFcuName(String fcuName) {
		this.fcuName = fcuName;
	}

	public String getLcuName() {
		return lcuName;
	}

	public void setLcuName(String lcuName) {
		this.lcuName = lcuName;
	}
	
	
	public List<String> getValueList() {
		if(CollectionUtils.isEmpty(valueList)) {
			valueList = new ArrayList<String>();
			if(CollectionUtils.isNotEmpty(authorityList)) {
				for(AuthorityVo authorityVo : this.authorityList) {
					if(authorityVo.getType().equals(GroupSearch.ROLE.getValue())) {
						valueList.add(GroupSearch.ROLE.getValuePlugin() + authorityVo.getUuid());
					}else if(authorityVo.getType().equals(GroupSearch.USER.getValue())){
						valueList.add(GroupSearch.USER.getValuePlugin() + authorityVo.getUuid());
					}else if(authorityVo.getType().equals(GroupSearch.TEAM.getValue())){
						valueList.add(GroupSearch.TEAM.getValuePlugin() + authorityVo.getUuid());
					}
				}
			}
		}
		return valueList;
	}

	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}

	public String getDefaultUser() {
		return defaultUser;
	}

	public void setDefaultUser(String defaultUser) {
		this.defaultUser = defaultUser;
	}

	public String getDefaultType() {
		return defaultType;
	}

	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}

	public Integer getIsCanEdit() {
		return isCanEdit;
	}

	public void setIsCanEdit(Integer isCanEdit) {
		this.isCanEdit = isCanEdit;
	}

	public Integer getIsCanRole() {
		return isCanRole;
	}

	public void setIsCanRole(Integer isCanRole) {
		this.isCanRole = isCanRole;
	}

	public List<AuthorityVo> getAuthorityList() {
		return authorityList;
	}

	public void setAuthorityList(List<AuthorityVo> authorityList) {
		this.authorityList = authorityList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getTeamUuidList() {
		return teamUuidList;
	}

	public void setTeamUuidList(List<String> teamUuidList) {
		this.teamUuidList = teamUuidList;
	}

	public List<String> getRoleNameList() {
		return roleNameList;
	}

	public void setRoleNameList(List<String> roleNameList) {
		this.roleNameList = roleNameList;
	}



	public enum DashBoardType {
		SYSTEM("system", "系统分类"), CUSTOM("custom", "自定义");
		private String value;
		private String name;

		private DashBoardType(String _value, String _name) {
			this.value = _value;
			this.name = _name;
		}

		public String getValue() {
			return value;
		}

		public String getName() {
			return name;
		}

		public static String getValue(String _value) {
			for (DashBoardType s : DashBoardType.values()) {
				if (s.getValue().equals(_value)) {
					return s.getValue();
				}
			}
			return null;
		}

		public static String getName(String _value) {
			for (DashBoardType s : DashBoardType.values()) {
				if (s.getValue().equals(_value)) {
					return s.getName();
				}
			}
			return "";
		}

	}
}

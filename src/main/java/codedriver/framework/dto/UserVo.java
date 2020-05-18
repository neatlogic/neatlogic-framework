package codedriver.framework.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class UserVo extends BasePageVo {
	private transient String keyword;
	@EntityField(name = "用户uuid", type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "用户id", type = ApiParamType.STRING)
	private String userId;
	@EntityField(name = "用户姓名", type = ApiParamType.STRING)
	private String userName;
	private String pinyin;
	private String tenant;
	@EntityField(name = "邮箱", type = ApiParamType.STRING)
	private String email;
	private String password;
	private String roleUuid;
	@EntityField(name = "是否激活(1:激活;0:未激活)", type = ApiParamType.INTEGER)
	private Integer isActive;
	@EntityField(name = "电话", type = ApiParamType.STRING)
	private String phone;
	private String dept;
	private String company;
	private String position;
	@EntityField(name = "其他属性", type = ApiParamType.STRING)
	private String userInfo;
	private String teamUuid;
	private String auth;
	private String authGroup;
	private JSONObject userInfoObj;
	
	@EntityField(name = "用户所在组uuid列表", type = ApiParamType.JSONARRAY)
	private List<String> teamUuidList = new ArrayList<>();
	private List<String> teamNameList = new ArrayList<>();
	
	@EntityField(name = "用户角色uuid列表", type = ApiParamType.JSONARRAY)
	private List<String> roleNameList = new ArrayList<>();
	private List<String> roleDescriptionList = new ArrayList<>();
	
	@EntityField(name = "用户角色信息列表", type = ApiParamType.JSONARRAY)
	private List<RoleVo> roleList = new ArrayList<>();
	@EntityField(name = "用户所在组信息列表", type = ApiParamType.JSONARRAY)
	private List<TeamVo> teamList = new ArrayList<>();
	@EntityField(name = "用户权限信息列表", type = ApiParamType.JSONARRAY)
	private List<UserAuthVo> userAuthList = new ArrayList<>();

	public UserVo() {

	}

	public List<UserAuthVo> getUserAuthList() {
		return userAuthList;
	}

	public void setUserAuthList(List<UserAuthVo> userAuthList) {
		this.userAuthList = userAuthList;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getAuthGroup() {
		return authGroup;
	}

	public void setAuthGroup(String authGroup) {
		this.authGroup = authGroup;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPinyin() {
		if (StringUtils.isBlank(this.pinyin)) {
			// 新增或者编辑的情况，this.pinYin是为null的，需要对其进行处理，通过userName拿到拼音；不为空时直接返回this.pinYin
			if (StringUtils.isNotBlank(this.userName)) {
				HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
				format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
				format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
				format.setVCharType(HanyuPinyinVCharType.WITH_V);
				char[] ch = this.userName.trim().toCharArray();
				StringBuffer buffer = new StringBuffer("");
				for (int i = 0; i < ch.length; i++) {
					if (Character.toString(ch[i]).matches("[\u4e00-\u9fa5]")) {
						String[] temp;
						try {
							temp = PinyinHelper.toHanyuPinyinStringArray(ch[i], format);
							buffer.append(temp[0]);
						} catch (BadHanyuPinyinOutputFormatCombination | NullPointerException e) {
							// 无法翻译的生僻字，不必处理
						}
					} else {
						buffer.append(Character.toString(ch[i]));
					}
				}
				this.pinyin = buffer.toString();
			} else {
				this.pinyin = "";
			}
		}
		return this.pinyin;
	}

	public List<String> getTeamNameList() {
		if (CollectionUtils.isNotEmpty(teamList)){
			List<String> arrayList = new ArrayList<>();
			for (TeamVo teamVo : teamList){
				arrayList.add(teamVo.getName());
			}
			return arrayList;
		}
		return teamNameList;
	}

	public void setTeamNameList(List<String> teamNameList) {
		this.teamNameList = teamNameList;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		if (StringUtils.isNotBlank(password)) {
			if (!password.startsWith("{MD5}")) {
				password = DigestUtils.md5DigestAsHex(password.getBytes());
				password = "{MD5}" + password;
			}
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getRoleUuid() {
		return roleUuid;
	}

	public void setRoleUuid(String roleUuid) {
		this.roleUuid = roleUuid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public List<String> getTeamUuidList() {
		if(CollectionUtils.isEmpty(teamUuidList) && CollectionUtils.isNotEmpty(teamList)){
			for (TeamVo teamVo : teamList){
				teamUuidList.add(teamVo.getUuid());
			}
		}
		return teamUuidList;
	}

	public void setTeamUuidList(List<String> teamUuidList) {
		this.teamUuidList = teamUuidList;
	}

	public List<TeamVo> getTeamList() {
		return teamList;
	}

	public void setTeamList(List<TeamVo> teamList) {
		this.teamList = teamList;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public JSONObject getUserInfoObj() {
		if (userInfoObj == null && StringUtils.isNotBlank(userInfo)) {
			userInfoObj = JSONObject.parseObject(userInfo);
		}
		return userInfoObj;
	}

	public void setUserInfoObj(JSONObject userInfoObj) {
		this.userInfoObj = userInfoObj;
	}

	public List<String> getRoleDescriptionList() {
		if(CollectionUtils.isNotEmpty(roleList)) {
			roleDescriptionList = new ArrayList<String>();
			for(RoleVo role : roleList) {
				roleDescriptionList.add(role.getDescription());
			}
		}
		return roleDescriptionList;
	}

	public List<String> getRoleNameList() {
		if(CollectionUtils.isEmpty(roleNameList) && CollectionUtils.isNotEmpty(roleList)) {
			for(RoleVo role : roleList) {
				roleNameList.add(role.getName());
			}
		}
		return roleNameList;
	}

	public void setRoleNameList(List<String> roleNameList) {
		this.roleNameList = roleNameList;
	}

	public List<RoleVo> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<RoleVo> roleList) {
		this.roleList = roleList;
	}

//	@Override
//	public boolean equals(Object other) {
//		if (this == other)
//			return true;
//		if (other == null)
//			return false;
//		if (!(other instanceof UserVo))
//			return false;
//
//		final UserVo user = (UserVo) other;
//		try {
//			if (getUserId().equals(user.getUserId()))
//				return true;
//		} catch (Exception ex) {
//			return false;
//		}
//		return false;
//	}
//
//	@Override
//	public int hashCode() {
//		int result = getUserId().hashCode() * 7;
//		return result;
//	}
}

/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserVo extends BaseEditorVo implements Serializable {

    private static final long serialVersionUID = 3670529362145832083L;
    private Long id;
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
    @EntityField(name = "头像", type = ApiParamType.STRING)
    private String avatar;
    @EntityField(name = "VIP级别(0,1,2,3,4,5)", type = ApiParamType.ENUM)
    private Integer vipLevel;
    private String teamUuid;
    private String auth;
    private String authGroup;
    private JSONObject userInfoObj;

    @EntityField(name = "是否已删除", type = ApiParamType.ENUM)
    private Integer isDelete;

    @EntityField(name = "用户所在组的头衔", type = ApiParamType.ENUM)
    private String title;

    @EntityField(name = "用户所在组的头衔中文名", type = ApiParamType.ENUM)
    private String titleText;

    @EntityField(name = "用户所在组uuid列表", type = ApiParamType.JSONARRAY)
    private List<String> teamUuidList = new ArrayList<>();
    private List<String> teamNameList = new ArrayList<>();

    @EntityField(name = "用户角色uuid列表", type = ApiParamType.JSONARRAY)
    private List<String> roleUuidList = new ArrayList<>();
    //private List<String> roleNameList = new ArrayList<>();

    @EntityField(name = "用户角色信息列表", type = ApiParamType.JSONARRAY)
    private List<RoleVo> roleList = new ArrayList<>();
    @EntityField(name = "用户所在组信息列表", type = ApiParamType.JSONARRAY)
    private List<TeamVo> teamList = new ArrayList<>();
    @EntityField(name = "用户权限信息列表", type = ApiParamType.JSONARRAY)
    private List<UserAuthVo> userAuthList = new ArrayList<>();
    @JSONField(serialize = false)
    private String cookieAuthorization;
    @JSONField(serialize = false)
    private String authorization;
    @JSONField(serialize = false)
    private List<String> userUuidList;
    @JSONField(serialize = false)
    private List<String> parentTeamUuidList;
    @JSONField(serialize = false)
    private List<String> rangeList;

    /**
     * 用户所在分组角色列表(考虑穿透)
     */
    @EntityField(name = "用户所在分组角色列表", type = ApiParamType.JSONARRAY)
    private List<RoleVo> teamRoleList = new ArrayList<>();

    /**
     * 此字段专供前端使用，用于渲染头像时区分对象类型，取值范围[user,team,role]
     */
    @EntityField(name = "前端初始化类型，取值范围[user,team,role]", type = ApiParamType.STRING)
    private final String initType = GroupSearch.USER.getValue();
    /***
     * 此字段专供前端使用，用于渲染用户插件
     */
    @EntityField(name = "用户名(与userName取值相同)", type = ApiParamType.STRING)
    private String name;

    private int isMaintenanceMode;
    @JSONField(serialize = false)
    private Boolean isAutoGenerateId = true;

    @EntityField(name = "是否超级管理员", type = ApiParamType.BOOLEAN)
    private Boolean isSuperAdmin;
    private String source;

    @JSONField(serialize = false)
    @EntityField(name = "jwtVo", type = ApiParamType.BOOLEAN)
    private JwtVo jwtVo;

    public UserVo() {

    }

    public UserVo(String uuid, String userName) {
        this.uuid = uuid;
        this.userName = userName;
    }

    public UserVo(String uuid) {
        this.uuid = uuid;
    }

    public UserVo(String uuid, boolean isAutoGenerateId) {
        this.uuid = uuid;
        this.isAutoGenerateId = isAutoGenerateId;
    }

    public UserVo(String uuid, String userId, String userName) {
        this.uuid = uuid;
        this.userId = userId;
        this.userName = userName;
    }

    public UserVo(String uuid, String userId, String userName, String phone, String email, Integer isActive) {
        this.uuid = uuid;
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.isActive = isActive;
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

    public Long getId() {
        if (id == null && isAutoGenerateId) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (CollectionUtils.isEmpty(teamNameList) && CollectionUtils.isNotEmpty(teamList)) {
            List<String> arrayList = new ArrayList<>();
            for (TeamVo teamVo : teamList) {
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
            if (!password.startsWith("{MD5}") && !password.startsWith("{BS}")) {
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
        if (CollectionUtils.isEmpty(teamUuidList) && CollectionUtils.isNotEmpty(teamList)) {
            for (TeamVo teamVo : teamList) {
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

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        if (StringUtils.isBlank(avatar) && StringUtils.isNotBlank(userInfo)) {
            JSONObject jsonObject = JSONObject.parseObject(userInfo);
            avatar = jsonObject.getString("avatar");
        }
        return avatar;
    }

    public void setUserInfoObj(JSONObject userInfoObj) {
        this.userInfoObj = userInfoObj;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

	/*public List<String> getRoleDescriptionList() {
		if(CollectionUtils.isEmpty(roleNameList) && CollectionUtils.isNotEmpty(roleList)) {
			for(RoleVo role : roleList) {
				roleNameList.add(role.getDescription());
			}
		}
		return roleNameList;
	}*/

    public List<String> getRoleUuidList() {
        if (CollectionUtils.isEmpty(roleUuidList) && CollectionUtils.isNotEmpty(roleList)) {
            for (RoleVo role : roleList) {
                roleUuidList.add(role.getUuid());
            }
        }
        return roleUuidList;
    }

    public String getInitType() {
        return initType;
    }

    public String getName() {
        name = userName;
        return name;
    }

    public void setRoleUuidList(List<String> roleUuidList) {
        this.roleUuidList = roleUuidList;
    }

    public List<RoleVo> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RoleVo> roleList) {
        this.roleList = roleList;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserVo other = (UserVo) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

//	public List<String> getValueList() {
//		if(CollectionUtils.isNotEmpty(valueList)) {
//			for(int i =0; i<valueList.size();i++) {
//				valueList.set(i,valueList.get(i).replaceAll(GroupSearch.USER.getValuePlugin(),""));
//			}
//		}
//		return valueList;
//	}
//
//	public void setValueList(List<String> valueList) {
//		this.valueList = valueList;
//	}

    public String getCookieAuthorization() {
        return cookieAuthorization;
    }

    public void setCookieAuthorization(String cookieAuthorization) {
        this.cookieAuthorization = cookieAuthorization;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public List<String> getUserUuidList() {
        return userUuidList;
    }

    public void setUserUuidList(List<String> userUuidList) {
        this.userUuidList = userUuidList;
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

    public int getIsMaintenanceMode() {
        return isMaintenanceMode;
    }

    public void setIsMaintenanceMode(int isMaintenanceMode) {
        this.isMaintenanceMode = isMaintenanceMode;
    }

    public List<RoleVo> getTeamRoleList() {
        return teamRoleList;
    }

    public void setTeamRoleList(List<RoleVo> teamRoleList) {
        this.teamRoleList = teamRoleList;
    }

    public Boolean getIsSuperAdmin() {
        return this.isSuperAdmin;
    }

    public void setIsSuperAdmin(Boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public JwtVo getJwtVo() {
        return jwtVo;
    }

    public void setJwtVo(JwtVo jwtVo) {
        this.jwtVo = jwtVo;
    }
}

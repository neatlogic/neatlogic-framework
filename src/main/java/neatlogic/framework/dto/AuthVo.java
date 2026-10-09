package neatlogic.framework.dto;

import neatlogic.framework.auth.core.AuthBase;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.$;

/** 权限元数据，供权限管理和接口声明权限展示共用；展示名称及说明按当前语言返回。 */
public class AuthVo {

    public static final String AUTH_DELETE = "delete";
    public static final String AUTH_ADD = "add";
    public static final String AUTH_COVER = "cover";
    @EntityField(name = "framework.authvo.name", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "framework.authvo.displayname", type = ApiParamType.STRING)
    private String displayName;
    @EntityField(name = "common.description", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "framework.authvo.authgroupname", type = ApiParamType.STRING)
    private String authGroupName;
    @EntityField(name = "framework.authvo.authgroup", type = ApiParamType.STRING)
    private String authGroup;
    @EntityField(name = "framework.authvo.iscommercial", type = ApiParamType.BOOLEAN)
    private boolean isCommercial = false;
    @EntityField(name = "framework.authvo.usercount", type = ApiParamType.INTEGER)
    private int userCount;
    @EntityField(name = "framework.authvo.rolecount", type = ApiParamType.INTEGER)
    private int roleCount;
    @EntityField(name = "common.sort", type = ApiParamType.INTEGER)
    private int sort;

    public AuthVo() {

    }

    public AuthVo(String name, String displayName, String description, int sort) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.sort = sort;
    }

    public AuthVo(String name, String displayName, String description, ModuleGroupVo authGroup, int sort) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.authGroupName = authGroup.getGroupName();
        this.authGroup = authGroup.getGroup();
        this.sort = sort;
    }

    public AuthVo(AuthBase tmpAuth) {
        this.name = tmpAuth.getAuthName();
        this.displayName = tmpAuth.getAuthDisplayName();
        this.description = tmpAuth.getAuthIntroduction();
        this.authGroupName = tmpAuth.getAuthGroup();
        this.sort = tmpAuth.getSort();
    }

    public boolean isCommercial() {
        return isCommercial;
    }

    public void setCommercial(boolean commercial) {
        isCommercial = commercial;
    }

    public String getDescription() {
        return $.t(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getRoleCount() {
        return roleCount;
    }

    public void setRoleCount(int roleCount) {
        this.roleCount = roleCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return $.t(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getAuthGroupName() {
        return authGroupName;
    }

    public void setAuthGroupName(String authGroupName) {
        this.authGroupName = authGroupName;
    }

    public String getAuthGroup() {
        return authGroup;
    }

    public void setAuthGroup(String authGroup) {
        this.authGroup = authGroup;
    }
}

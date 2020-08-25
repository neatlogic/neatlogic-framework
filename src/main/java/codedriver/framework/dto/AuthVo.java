package codedriver.framework.dto;

public class AuthVo {

    public static final String AUTH_DELETE = "delete";
    public static final String AUTH_ADD = "add";
    public static final String AUTH_COVER = "cover";
    private String name;
    private String displayName;
    private String description;
    private int userCount;
    private int roleCount;

    public String getDescription() {
        return description;
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
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

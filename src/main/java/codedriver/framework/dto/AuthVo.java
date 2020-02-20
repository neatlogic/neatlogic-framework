package codedriver.framework.dto;

public class AuthVo {

    public static final String AUTH_DELETE = "delete";
    public static final String AUTH_ADD = "add";
    public static final String AUTH_COVER = "cover";
    private String name;
    private String displayName;

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

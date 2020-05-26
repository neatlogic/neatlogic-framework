package codedriver.framework.reminder.dto;

/**
 * @program: balantflow
 * @description: 实时动态订阅实体类
 * @create: 2019-09-10 20:10
 **/
public class GlobalReminderSubscribeVo {
    private Long id;
    private String handler;
    private String userUuid;
    private String userName;
    private String param;
    private int isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}

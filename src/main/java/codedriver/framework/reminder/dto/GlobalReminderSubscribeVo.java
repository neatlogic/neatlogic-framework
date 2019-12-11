package codedriver.framework.reminder.dto;

/**
 * @program: balantflow
 * @description: 实时动态订阅实体类
 * @create: 2019-09-10 20:10
 **/
public class GlobalReminderSubscribeVo {
    private Long id;
    private Long reminderId;
    private String userId;
    private String param;
    private int isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReminderId() {
        return reminderId;
    }

    public void setReminderId(Long reminderId) {
        this.reminderId = reminderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

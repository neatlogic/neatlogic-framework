package codedriver.framework.reminder.dto;

/**
 * @program: balantflow
 * @description: 消息查询参数封装类
 * @create: 2019-08-12 11:01
 **/
public class ReminderMessageSearchVo {
    public static final Integer DEFAULT_SHOW_COUNT = 5;
    public static final Integer DEFAULT_ADD_COUNT = 2;
    private String userUuid;
    private Integer messageCount;
    private String startTime;
    private String endTime;
    private Long messageId;

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}

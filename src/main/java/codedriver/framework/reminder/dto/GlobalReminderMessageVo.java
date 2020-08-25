package codedriver.framework.reminder.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * @program: balantflow
 * @description: 实时动态消息实体类
 * @create: 2019-08-09 11:06
 **/
public class GlobalReminderMessageVo implements Comparable<GlobalReminderMessageVo> {
    Logger logger = LoggerFactory.getLogger(GlobalReminderMessageVo.class);
    public static final int SHOW_DAY = 3;
    private Long id;
    private String handler;
    private String title;
    private String content;
    private String createTime;
    private String fromUser;
    private String fromUserName;
    private String param;
    private int isActive;
    private int isNew;
    private int isKeep;

    private GlobalReminderHandlerVo reminderVo;

    private GlobalReminderSubscribeVo reminderSubscribeVo;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime()  {
        if (createTime != null && createTime != ""){
            SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long min = 60 * 1000;
            long hour = 60 * 60 * 1000;
            try {
                long time = System.currentTimeMillis() - fd.parse(createTime).getTime();
                return time < min ? "现在" : time < hour ? time/min + "分钟前" : createTime.substring(5, createTime.length());
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getIsKeep() {
        return isKeep;
    }

    public void setIsKeep(int isKeep) {
        this.isKeep = isKeep;
    }

    public GlobalReminderHandlerVo getReminderVo() {
        return reminderVo;
    }

    public void setReminderVo(GlobalReminderHandlerVo reminderVo) {
        this.reminderVo = reminderVo;
    }

    public GlobalReminderSubscribeVo getReminderSubscribeVo() {
        return reminderSubscribeVo;
    }

    public void setReminderSubscribeVo(GlobalReminderSubscribeVo reminderSubscribeVo) {
        this.reminderSubscribeVo = reminderSubscribeVo;
    }

    @Override
    public int compareTo(GlobalReminderMessageVo o) {
        return o.getId().compareTo(this.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof GlobalReminderMessageVo)) {
            return false;
        }
        GlobalReminderMessageVo that = (GlobalReminderMessageVo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + id.hashCode();
        return result;
    }
}

package neatlogic.framework.message.dto;

/**
 * @Title: TriggerMessageCountVo
 * @Package neatlogic.framework.message.dto
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/2/22 18:46

 **/
public class TriggerMessageCountVo {
    private String notifyPolicyHandler;
    private String trigger;
    private Integer count;
    private Integer isRead;

    public String getNotifyPolicyHandler() {
        return notifyPolicyHandler;
    }

    public void setNotifyPolicyHandler(String notifyPolicyHandler) {
        this.notifyPolicyHandler = notifyPolicyHandler;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}

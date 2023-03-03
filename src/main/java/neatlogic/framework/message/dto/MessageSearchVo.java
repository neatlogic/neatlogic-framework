package neatlogic.framework.message.dto;

import neatlogic.framework.common.dto.BaseEditorVo;

import java.util.Date;
import java.util.List;

/**
 * @Title: MessageMessageSearchVo
 * @Package neatlogic.framework.message.dto
 * @Description: 消息查询Vo
 * @Author: linbq
 * @Date: 2021/1/4 20:56
 **/
public class MessageSearchVo extends BaseEditorVo {
    private Long messageId;
    private Long minMessageId;
    private Long maxMessageId;
    private String userUuid;
    private List<String> teamUuidList;
    private List<String> roleUuidList;
    private Date startTime;
    private Date endTime;
    private List<String> handlerList;
    private List<String> triggerList;
    private String notifyPolicyHandler;
    private Integer isShow;
    private Integer isRead;
    private Date expiredTime;
    List<Long> messageIdList;
    public MessageSearchVo(){

    }

    public MessageSearchVo(String userUuid, Long messageId) {
        this.messageId = messageId;
        this.userUuid = userUuid;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getMinMessageId() {
        return minMessageId;
    }

    public void setMinMessageId(Long minMessageId) {
        this.minMessageId = minMessageId;
    }

    public Long getMaxMessageId() {
        return maxMessageId;
    }

    public void setMaxMessageId(Long maxMessageId) {
        this.maxMessageId = maxMessageId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public List<String> getTeamUuidList() {
        return teamUuidList;
    }

    public void setTeamUuidList(List<String> teamUuidList) {
        this.teamUuidList = teamUuidList;
    }

    public List<String> getRoleUuidList() {
        return roleUuidList;
    }

    public void setRoleUuidList(List<String> roleUuidList) {
        this.roleUuidList = roleUuidList;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<String> getHandlerList() {
        return handlerList;
    }

    public void setHandlerList(List<String> handlerList) {
        this.handlerList = handlerList;
    }

    public List<String> getTriggerList() {
        return triggerList;
    }

    public void setTriggerList(List<String> triggerList) {
        this.triggerList = triggerList;
    }

    public String getNotifyPolicyHandler() {
        return notifyPolicyHandler;
    }

    public void setNotifyPolicyHandler(String notifyPolicyHandler) {
        this.notifyPolicyHandler = notifyPolicyHandler;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public List<Long> getMessageIdList() {
        return messageIdList;
    }

    public void setMessageIdList(List<Long> messageIdList) {
        this.messageIdList = messageIdList;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}

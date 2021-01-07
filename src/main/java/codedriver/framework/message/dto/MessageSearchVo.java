package codedriver.framework.message.dto;

import codedriver.framework.common.dto.BaseEditorVo;

import java.util.Date;
import java.util.List;

/**
 * @Title: MessageMessageSearchVo
 * @Package codedriver.framework.message.dto
 * @Description: 消息查询Vo
 * @Author: linbq
 * @Date: 2021/1/4 20:56
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class MessageSearchVo extends BaseEditorVo {
    private Long messageId;
    private String userUuid;
    private List<String> teamUuidList;
    private List<String> roleUuidList;
    private Date startTime;
    private Date endTime;
    private List<String> handlerList;

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
}

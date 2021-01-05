package codedriver.framework.news.dto;

import codedriver.framework.common.dto.BaseEditorVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Title: NewsMessageSearchVo
 * @Package codedriver.framework.news.dto
 * @Description: 消息查询Vo
 * @Author: linbq
 * @Date: 2021/1/4 20:56
 **/
public class NewsMessageSearchVo extends BaseEditorVo {
    private Long newsMessageId;
    private Integer isDelete;
    private String userUuid;
    private List<String> teamUuidList;
    private List<String> roleUuidList;
    private Date startTime;
    private Date endTime;
    private List<String> handlerList;

    public NewsMessageSearchVo(){

    }

    public NewsMessageSearchVo(String userUuid, Long newsMessageId) {
        this.newsMessageId = newsMessageId;
        this.userUuid = userUuid;
    }

    public Long getNewsMessageId() {
        return newsMessageId;
    }

    public void setNewsMessageId(Long newsMessageId) {
        this.newsMessageId = newsMessageId;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
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

package codedriver.framework.reminder.dto;


import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @program: balantflow
 * @description: 动态消息封装类
 * @create: 2019-08-14 14:33
 **/
public class ReminderMessageVo {
    private String title;
    private String content;
    private String fromUser;
    private List<String> receiverList;
    private List<Long> receiveTeamList;
    private JSONObject paramObj;

    public ReminderMessageVo() {
    }

    public ReminderMessageVo(String title, String content, String fromUser, List<String> receiverList, List<Long> receiveTeamList, JSONObject paramObj) {
        this.title = title;
        this.content = content;
        this.fromUser = fromUser;
        this.receiverList = receiverList;
        this.receiveTeamList = receiveTeamList;
        this.paramObj = paramObj;
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

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public List<String> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<String> receiverList) {
        this.receiverList = receiverList;
    }

    public List<Long> getReceiveTeamList() {
        return receiveTeamList;
    }

    public void setReceiveTeamList(List<Long> receiveTeamList) {
        this.receiveTeamList = receiveTeamList;
    }

    public JSONObject getParamObj() {
        return paramObj;
    }

    public void setParamObj(JSONObject paramObj) {
        this.paramObj = paramObj;
    }
}

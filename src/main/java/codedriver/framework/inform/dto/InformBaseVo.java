package codedriver.framework.inform.dto;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @program: codedriver
 * @description: 通知类
 * @create: 2019-12-09 09:54
 **/
public class InformBaseVo {
    private String title;
    private String content;
    private String pluginId;
    private List<String> toUserIdList;
    private List<String> toTeamIdList;
    private String fromUser;
    private JSONObject paramObj;
    private String templateContent;
    private String templateTitle;

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

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public List<String> getToUserIdList() {
        return toUserIdList;
    }

    public void setToUserIdList(List<String> toUserIdList) {
        this.toUserIdList = toUserIdList;
    }

    public List<String> getToTeamIdList() {
        return toTeamIdList;
    }

    public void setToTeamIdList(List<String> toTeamIdList) {
        this.toTeamIdList = toTeamIdList;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public JSONObject getParamObj() {
        return paramObj;
    }

    public void setParamObj(JSONObject paramObj) {
        this.paramObj = paramObj;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }
}

package codedriver.framework.inform.dto;

import codedriver.framework.dto.UserVo;
import com.alibaba.fastjson.JSONObject;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 11:38
 **/
public class MessageVo {
    private String title;
    private String content;
    private String fromUser;
    private UserVo toUser;
    private JSONObject paramObj;

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

    public UserVo getToUser() {
        return toUser;
    }

    public void setToUser(UserVo toUser) {
        this.toUser = toUser;
    }

    public JSONObject getParamObj() {
        return paramObj;
    }

    public void setParamObj(JSONObject paramObj) {
        this.paramObj = paramObj;
    }
}

package codedriver.framework.inform.dto;

import codedriver.framework.dto.UserVo;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 11:38
 **/
public class MessageBaseVo {
    private String title;
    private String content;
    private String fromUser;
    private List<UserVo> toUserList;
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

    public List<UserVo> getToUserList() {
        return toUserList;
    }

    public void setToUserList(List<UserVo> toUserList) {
        this.toUserList = toUserList;
    }

    public JSONObject getParamObj() {
        return paramObj;
    }

    public void setParamObj(JSONObject paramObj) {
        this.paramObj = paramObj;
    }
}

package codedriver.framework.news.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BaseEditorVo;
import codedriver.framework.notify.dto.NotifyReceiverVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Title: NewsMessageVo
 * @Package codedriver.framework.news.dto
 * @Description: 消息详情Vo
 * @Author: linbq
 * @Date: 2020/12/30 15:13
 **/
public class NewsMessageVo {
    @EntityField(name = "消息id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "标题", type = ApiParamType.STRING)
    private String title;
    @EntityField(name = "内容", type = ApiParamType.STRING)
    private String content;
    @EntityField(name = "消息类型处理器全类名", type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "发送时间", type = ApiParamType.LONG)
    private Date fcd;
    @EntityField(name = "是否已读", type = ApiParamType.INTEGER)
    private Integer isRead;
    @EntityField(name = "弹框方式", type = ApiParamType.STRING)
    private String popUp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Date getFcd() {
        return fcd;
    }

    public void setFcd(Date fcd) {
        this.fcd = fcd;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getPopUp() {
        return popUp;
    }

    public void setPopUp(String popUp) {
        this.popUp = popUp;
    }
}

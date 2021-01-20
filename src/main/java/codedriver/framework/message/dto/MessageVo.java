package codedriver.framework.message.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.notify.dto.NotifyVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * @Title: MessageVo
 * @Package codedriver.framework.message.dto
 * @Description: 消息详情Vo
 * @Author: linbq
 * @Date: 2020/12/30 15:13
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class MessageVo {
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
    @EntityField(name = "弹框方式", type = ApiParamType.STRING)
    private String popUp;
    @JSONField(serialize = false)
    private transient Integer isRead;

    public MessageVo() {
    }
    public MessageVo(NotifyVo notifyVo) {
        this.title = notifyVo.getTitle();
        this.content = notifyVo.getContent();
        this.handler = notifyVo.getMessageHandlerClass().getName();
        this.fcd = notifyVo.getFcd();
    }

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
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

    public String getPopUp() {
        return popUp;
    }

    public void setPopUp(String popUp) {
        this.popUp = popUp;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}

package codedriver.framework.message.dto;

/**
 * @Title: MessageMessageRecipientVo
 * @Package codedriver.framework.message.dto
 * @Description: 消息接收者Vo
 * @Author: linbq
 * @Date: 2020/12/30 16:33
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class MessageRecipientVo {
    private Long messageId;
    private String type;
    private String uuid;

    public MessageRecipientVo(){

    }
    public MessageRecipientVo(Long messageId, String type, String uuid) {
        this.messageId = messageId;
        this.type = type;
        this.uuid = uuid;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

package neatlogic.framework.message.dto;

/**
 * @Title: MessageMessageRecipientVo
 * @Package neatlogic.framework.message.dto
 * @Description: 消息接收者Vo
 * @Author: linbq
 * @Date: 2020/12/30 16:33
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

package neatlogic.framework.mq.dto;

/** 消息代理的发送回执，不代表消费者已经处理消息。 */
public class MqSendResultVo {
    private String handler;
    private String destination;
    private String messageId;
    private Integer partition;
    private Long offset;

    /** 获取消息队列处理器标识。 */
    public String getHandler() {
        return handler;
    }
    /** 设置消息队列处理器标识。 */
    public void setHandler(String handler) {
        this.handler = handler;
    }
    /** 获取实际发送目标。 */
    public String getDestination() {
        return destination;
    }
    /** 设置实际发送目标。 */
    public void setDestination(String destination) {
        this.destination = destination;
    }
    /** 获取代理分配的消息标识。 */
    public String getMessageId() {
        return messageId;
    }
    /** 设置代理分配的消息标识。 */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    /** 获取Kafka分区。 */
    public Integer getPartition() {
        return partition;
    }
    /** 设置Kafka分区。 */
    public void setPartition(Integer partition) {
        this.partition = partition;
    }
    /** 获取Kafka消息偏移量。 */
    public Long getOffset() {
        return offset;
    }
    /** 设置Kafka消息偏移量。 */
    public void setOffset(Long offset) {
        this.offset = offset;
    }
}

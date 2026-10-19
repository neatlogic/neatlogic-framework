package neatlogic.framework.exception.mq;

/** 等待发送确认超时或被中断，消息可能已经到达代理，调用方不得自动重发。 */
public class MqSendUnconfirmedException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String handler;
    private final String destination;

    /** 兼容不携带目标信息的通用调用方。 */
    public MqSendUnconfirmedException(String message, Throwable cause) {
        this(null, null, message, cause);
    }

    /** 保留实际目标，审计不需要重复计算各代理的命名规则。 */
    public MqSendUnconfirmedException(String handler, String destination, String message, Throwable cause) {
        super(message, cause);
        this.handler = handler;
        this.destination = destination;
    }

    /** 获取发送处理器标识。 */
    public String getHandler() { return handler; }
    /** 获取实际发送目标。 */
    public String getDestination() { return destination; }
}

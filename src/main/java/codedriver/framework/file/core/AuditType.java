/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

public enum AuditType implements IAuditType {
    API_AUDIT("apiaudit", true, 0, 256, "apiaudit.log", "30mb", "%name %d{yyyy-MM-dd HH:mm:ss.SSS} - %n%msg%n");
    private String type;
    private boolean async;
    private int discardingThreshold;
    private int queueSize;
    private String fileName;
    private String maxFileSize;
    private String messagePattern;

    AuditType(
            String type,
            boolean async,
            int discardingThreshold,
            int queueSize,
            String fileName,
            String maxFileSize,
            String messagePattern) {
        this.type = type;
        this.async = async;
        this.discardingThreshold = discardingThreshold;
        this.queueSize = queueSize;
        this.fileName = fileName;
        this.maxFileSize = maxFileSize;
        this.messagePattern = messagePattern;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean getAsync() {
        return async;
    }

    @Override
    public int getDiscardingThreshold() {
        return discardingThreshold;
    }

    @Override
    public int getQueueSize() {
        return queueSize;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getMaxFileSize() {
        return maxFileSize;
    }

    @Override
    public String getMessagePattern() {
        return messagePattern;
    }
}

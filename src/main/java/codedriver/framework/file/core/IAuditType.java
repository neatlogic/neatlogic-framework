/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

/**
 * 日志文件类型
 */
public interface IAuditType {
    /**
     * 日志类型
     * @return
     */
    String getType();

    /**
     * 文件名
     * @return
     */
    String getFileName();

    /**
     * 归档文件大小
     * @return
     */
    String getMaxFileSize();

    /**
     * 消息模式
     * @return
     */
    String getMessagePattern();
}

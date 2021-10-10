/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.heartbeat.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.asynchronization.thread.CodeDriverThread;

public class HeartbreakHandlerThread extends CodeDriverThread {
    private final Logger logger = LoggerFactory.getLogger(HeartbreakHandlerThread.class);
    private final IHeartbreakHandler observer;
    private final Integer serverId;

    public HeartbreakHandlerThread(IHeartbreakHandler observer, Integer serverId) {
        super("HEARTBREAK-HANDLER");
        this.observer = observer;
        this.serverId = serverId;
    }

    @Override
    protected void execute() {
        String oldThreadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName("HEARTBREAK-HANDLER");
            observer.whenServerInactivated(serverId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            Thread.currentThread().setName(oldThreadName);
        }
    }

}

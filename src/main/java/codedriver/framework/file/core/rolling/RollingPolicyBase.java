/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.rolling;

import codedriver.framework.file.core.appender.FileAppender;

/**
 * 实现大多数（并非所有）滚动策略通用的方法。目前，此类方法仅限于压缩模式的getter/setter。
 */
public abstract class RollingPolicyBase implements RollingPolicy {

    private FileAppender<?> parent;

    private boolean started;

    public boolean isStarted() {
        return started;
    }

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public void setParent(FileAppender<?> appender) {
        this.parent = appender;
    }

    public String getParentsRawFileProperty() {
        return parent.rawFileProperty();
    }
}

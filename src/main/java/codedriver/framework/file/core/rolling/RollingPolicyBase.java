/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package codedriver.framework.file.core.rolling;

import codedriver.framework.file.core.appender.FileAppender;

/**
 * 实现大多数（并非所有）滚动策略通用的方法。目前，此类方法仅限于压缩模式的getter/setter。
 */
public abstract class RollingPolicyBase implements RollingPolicy {

    FileNamePattern fileNamePattern;
    // fileNamePatternStr始终被斜杠化，请参阅setter
    protected String fileNamePatternStr;

    private FileAppender<?> parent;

    private boolean started;

    public void setFileNamePattern(String fnp) {
        fileNamePatternStr = fnp;
    }

    public FileNamePattern getFileNamePattern() {
        return fileNamePattern;
    }

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

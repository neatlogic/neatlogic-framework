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

import ch.qos.logback.core.util.FileSize;
import codedriver.framework.file.core.util.DefaultInvocationGate;
import codedriver.framework.file.core.util.InvocationGate;

import java.io.File;

/**
 * SizeBasedTriggeringPolicy查看当前写入的文件的大小。
 * 如果文件的大小超过指定的大小，则使用SizeBased Triggering Policy的FileAppender将滚动文件并创建新文件。
 */
public class SizeBasedTriggeringPolicy<E> extends TriggeringPolicyBase<E> {
    /**
     * 默认最大文件大小。
     */
    public static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    FileSize maxFileSize = new FileSize(DEFAULT_MAX_FILE_SIZE);

    public SizeBasedTriggeringPolicy() {
    }

    InvocationGate invocationGate = new DefaultInvocationGate();

    public boolean isTriggeringEvent(final File activeFile, final E event) {
        long now = System.currentTimeMillis();
        if (invocationGate.isTooSoon(now)) {
//            System.out.println("isTooSoon: " + true);
            return false;
        }
        return (activeFile.length() >= maxFileSize.getSize());
    }

    public void setMaxFileSize(FileSize aMaxFileSize) {
        this.maxFileSize = aMaxFileSize;
    }

}

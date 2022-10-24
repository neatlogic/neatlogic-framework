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
package codedriver.framework.file.core.layout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

abstract public class LayoutBase<E> implements Layout<E> {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    protected boolean started;

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public String getContentType() {
        return "text/plain";
    }

    @Override
    public String getFileHeader() {
        return "fileHeader##########" + LocalDateTime.now().format(dateTimeFormatter) + "#########fileHeader";
    }

    @Override
    public String getFileFooter() {
        return "fileFooter##########" + LocalDateTime.now().format(dateTimeFormatter) + "#########fileFooter";
    }
}

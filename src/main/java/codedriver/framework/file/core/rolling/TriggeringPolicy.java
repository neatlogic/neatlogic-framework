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

import ch.qos.logback.core.spi.LifeCycle;

import java.io.File;

/**
 * TriggeringPolicy控制发生滚动的条件。这些条件包括一天中的时间、文件大小、外部事件、日志请求或其组合。
 * */

public interface TriggeringPolicy<E> extends LifeCycle {

    /**
     * 此时是否应触发滚动？
     *
     * @param activeFile 对当前活动文件的引用。
     * @param event 对当前事件的引用。
     * @return 如果发生翻滚，则为true。
     */
    boolean isTriggeringEvent(final File activeFile, final E event);
}

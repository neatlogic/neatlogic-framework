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

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.LifeCycle;
import codedriver.framework.file.core.appender.FileAppender;

/**
 * RollingPolicy负责执行活动日志文件的滚动。RollingPolicy还负责提供活动日志文件，即日志输出将被定向到的活动文件
 */
public interface RollingPolicy extends LifeCycle {

    /**
     * 根据实现策略滚动日志文件。
     *
     * 此方法由{@link RollingFileAppender}调用，通常是在其{@linkTriggeringPolicy}的要求下调用的。
     *
     * @throws RolloverFailure 如果滚动操作因任何原因失败，则引发此错误。
     */
    void rollover() throws RolloverFailure;

    int rollover(int currentIndex) throws RolloverFailure;

    /**
     * 获取活动日志文件的名称。
     *
     * <p>对于{@link TimeBasedRollingPolicy}等实现，此方法返回一个新的文件名，实际输出将在其中发送。
     *
     * <p>在其他实现中，此方法可能返回FileAppender的file属性。
     */
    String getActiveFileName();

    /**
     * 此方法允许RollingPolicy实现了解其包含的appender。
     *
     * @param appender
     */

    void setParent(FileAppender<?> appender);

    int getMaxIndex();

    int getMinIndex();

    void setMaxIndex(int maxIndex);
}

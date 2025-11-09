/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.file.core.rolling;

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.spi.LifeCycle;
import neatlogic.framework.file.core.appender.FileAppender;

/**
 * RollingPolicy负责执行活动日志文件的滚动。RollingPolicy还负责提供活动日志文件，即日志输出将被定向到的活动文件
 */
public interface RollingPolicy extends LifeCycle {

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

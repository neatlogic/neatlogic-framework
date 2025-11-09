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

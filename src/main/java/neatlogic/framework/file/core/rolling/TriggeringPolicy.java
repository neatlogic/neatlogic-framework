/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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

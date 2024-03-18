/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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

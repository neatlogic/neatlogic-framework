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

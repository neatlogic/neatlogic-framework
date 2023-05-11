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

import ch.qos.logback.core.rolling.RolloverFailure;
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

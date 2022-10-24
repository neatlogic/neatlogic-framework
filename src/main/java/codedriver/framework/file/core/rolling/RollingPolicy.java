/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.rolling;

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.LifeCycle;
import codedriver.framework.file.core.appender.FileAppender;

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

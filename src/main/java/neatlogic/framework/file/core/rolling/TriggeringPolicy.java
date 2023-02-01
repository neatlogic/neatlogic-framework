/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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

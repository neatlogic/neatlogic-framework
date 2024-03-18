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

package neatlogic.framework.file.core.appender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 与AppenderBase类似，只是派生的appender需要自己处理线程同步。
 * @param <E>
 */
public abstract class UnsynchronizedAppenderBase<E> implements Appender<E> {
    private Logger logger = LoggerFactory.getLogger(UnsynchronizedAppenderBase.class);
    protected boolean started = false;

    // 使用ThreadLocal而不是布尔值，每个doAppend调用增加75纳秒。这是可以接受的，因为doAppend在真正的appender上至少需要几微秒
    /**
     * 该标识变量防止appender重复调用自己的doAppend方法。
     */
    private ThreadLocal<Boolean> guard = new ThreadLocal<Boolean>();

    protected String name;

    private int statusRepeatCount = 0;
    private int exceptionCount = 0;

    static final int ALLOWED_REPEATS = 3;

    public void doAppend(E eventObject) {
//        System.out.println(1);
        // 警告：保护检查必须是doAppend()方法中的第一条语句。防止再次进入。
        if (Boolean.TRUE.equals(guard.get())) {
            return;
        }

        try {
            guard.set(Boolean.TRUE);

            if (!this.started) {
                if (statusRepeatCount++ < ALLOWED_REPEATS) {
                    logger.warn("试图追加到未启动的追加器 [" + name + "].");
                }
                return;
            }

            // 调用派生类的append实现
            this.append(eventObject);

        } catch (Exception e) {
            if (exceptionCount++ < ALLOWED_REPEATS) {
                logger.error("追加器 [" + name + "] 无法追加。");
                logger.error(e.getMessage(), e);
            }
        } finally {
            guard.set(Boolean.FALSE);
        }
    }

    abstract protected void append(E eventObject);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public String toString() {
        return this.getClass().getName() + "[" + name + "]";
    }
}

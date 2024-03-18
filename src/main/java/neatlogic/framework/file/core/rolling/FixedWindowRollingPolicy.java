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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 滚动时，FixedWindowRollingPolicy根据固定窗口算法重命名文件。
 */
public class FixedWindowRollingPolicy extends RollingPolicyBase {
    private Logger logger = LoggerFactory.getLogger(FixedWindowRollingPolicy.class);
    int maxIndex;
    int minIndex;

    private static int MAX_WINDOW_SIZE = 100;

    public FixedWindowRollingPolicy() {
        minIndex = 1;
        maxIndex = 20;
    }

    public void start() {
        if (getParentsRawFileProperty() == null) {
            logger.error("在使用此滚动策略之前，必须设置文件名属性。");
            throw new IllegalStateException("必须设置“文件”选项。");
        }

        if (maxIndex < minIndex) {
            maxIndex = minIndex;
        }

        final int maxWindowSize = getMaxWindowSize();
        if ((maxIndex - minIndex) > maxWindowSize) {
            maxIndex = minIndex + maxWindowSize;
        }
        super.start();
    }

    /**
     * 如果需要，子类可以重写此方法以增加最大窗口大小。
     * @return
     */
    protected int getMaxWindowSize() {
        return MAX_WINDOW_SIZE;
    }

    public int rollover(int currentIndex) throws RolloverFailure {
        int nextIndex;
        if (currentIndex < maxIndex) {
            nextIndex = currentIndex + 1;
        } else {
            nextIndex = minIndex;
        }
        File file = new File(getParentsRawFileProperty() + "." + nextIndex);
        if (file.exists()) {
            file.delete();
        }
        return nextIndex;
    }

    /**
     * 返回父级的RawFile属性的值。
     */
    public String getActiveFileName() {
        return getParentsRawFileProperty();
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public int getMinIndex() {
        return minIndex;
    }

    public void setMaxIndex(int maxIndex) {
        this.maxIndex = maxIndex;
    }

    public void setMinIndex(int minIndex) {
        this.minIndex = minIndex;
    }
}

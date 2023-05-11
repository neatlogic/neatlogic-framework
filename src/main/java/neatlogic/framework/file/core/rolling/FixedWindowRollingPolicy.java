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

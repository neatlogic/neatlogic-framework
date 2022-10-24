/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.rolling;

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

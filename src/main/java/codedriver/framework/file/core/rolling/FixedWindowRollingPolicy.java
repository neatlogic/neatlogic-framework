/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package codedriver.framework.file.core.rolling;

import ch.qos.logback.core.rolling.RolloverFailure;
import codedriver.framework.file.core.pattern.IntegerTokenConverter;
import codedriver.framework.file.core.util.RenameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 滚动时，FixedWindowRollingPolicy根据固定窗口算法重命名文件。
 */
public class FixedWindowRollingPolicy extends RollingPolicyBase {
    private Logger logger = LoggerFactory.getLogger(FixedWindowRollingPolicy.class);
    static final String FNP_NOT_SET = "在使用FixedWindowRollingPolicy之前，必须设置“FileNamePattern”属性。";
    int maxIndex;
    int minIndex;
    RenameUtil util = new RenameUtil();
//    Compressor compressor;

    public static final String ZIP_ENTRY_DATE_PATTERN = "yyyy-MM-dd_HHmm";

    /**
     * 窗户太大，比如说20多，几乎总是个坏主意。
     */
    private static int MAX_WINDOW_SIZE = 20;

    public FixedWindowRollingPolicy() {
        minIndex = 1;
        maxIndex = 7;
    }

    public void start() {

        if (fileNamePatternStr != null) {
            fileNamePattern = new FileNamePattern(fileNamePatternStr);
        } else {
            throw new IllegalStateException(FNP_NOT_SET);
        }

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

        IntegerTokenConverter itc = fileNamePattern.getIntegerTokenConverter();

        if (itc == null) {
            throw new IllegalStateException("FileNamePattern [" + fileNamePattern.getPattern() + "] does not contain a valid IntegerToken");
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

    public void rollover() throws RolloverFailure {

        // 在该方法中，可以确保关闭此处的活动日志文件。
        // 如果maxIndex<=0，则不需要进行文件重命名。
        if (maxIndex >= 0) {
            // 删除最旧的文件，让Windows满意。
            File file = new File(fileNamePattern.convertInt(maxIndex));

            if (file.exists()) {
                file.delete();
            }

            // Map {(maxIndex - 1), ..., minIndex} to {maxIndex, ..., minIndex+1}
            for (int i = maxIndex - 1; i >= minIndex; i--) {
                String toRenameStr = fileNamePattern.convertInt(i);
                File toRename = new File(toRenameStr);
                // 尝试重命名不存在的文件没有意义
                if (toRename.exists()) {
                    util.rename(toRenameStr, fileNamePattern.convertInt(i + 1));
                }
            }
            // 将活动文件名移动到最小
            util.rename(getActiveFileName(), fileNamePattern.convertInt(minIndex));
        }
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

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
package codedriver.framework.file.core.util;

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.helper.FileStoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 实用程序类来帮助解决重命名文件时遇到的问题。
 */
public class RenameUtil {

    private Logger logger = LoggerFactory.getLogger(RenameUtil.class);

    /**
     * 一种相对可靠的文件重命名方法，如果由于src和目标位于不同的卷上而失败，则可以通过复制重新命名。
     *
     * @param src
     * @param target
     * @throws RolloverFailure
     */
    public void rename(String src, String target) throws RolloverFailure {
        if (src.equals(target)) {
            return;
        }
        File srcFile = new File(src);

        if (srcFile.exists()) {
            File targetFile = new File(target);
            // 创建目标文件的父级目录
            createMissingTargetDirsIfNecessary(targetFile);

            boolean result = srcFile.renameTo(targetFile);

            if (!result) {
                Boolean areOnDifferentVolumes = areOnDifferentVolumes(srcFile, targetFile);
                if (Boolean.TRUE.equals(areOnDifferentVolumes)) {
                    renameByCopying(src, target);
                    return;
                }
            }
        } else {
            throw new RolloverFailure("File [" + src + "] does not exist.");
        }
    }



    /**
     * 尝试确定这两个文件是否位于不同的卷上。如果可以确定文件位于不同的卷上，则返回true。否则，如果在执行检查时出错，则返回false。
     *
     * @param srcFile
     * @param targetFile
     * @return 如果在不同的卷上，则为true；否则为false或发生错误
     */
    Boolean areOnDifferentVolumes(File srcFile, File targetFile) throws RolloverFailure {

        // 目标文件不一定存在，但鉴于此方法的调用层次结构，它的父文件必须存在
        File parentOfTarget = targetFile.getAbsoluteFile().getParentFile();

        if(parentOfTarget == null) {
            return null;
        }
        if(!parentOfTarget.exists()) {
            return null;
        }

        try {
            boolean onSameFileStore = FileStoreUtil.areOnSameFileStore(srcFile, parentOfTarget);
            return !onSameFileStore;
        } catch (RolloverFailure rf) {
            logger.warn("检查文件存储相等性时出错");
            return null;
        }
    }

    public void renameByCopying(String src, String target) throws RolloverFailure {

        FileUtil fileUtil = new FileUtil();
        fileUtil.copy(src, target);

        File srcFile = new File(src);
        if (!srcFile.delete()) {
            logger.warn("无法删除文件" + src);
        }

    }

    void createMissingTargetDirsIfNecessary(File toFile) throws RolloverFailure {
        boolean result = FileUtil.createMissingParentDirectories(toFile);
        if (!result) {
            throw new RolloverFailure("Failed to create parent directories for [" + toFile.getAbsolutePath() + "]");
        }
    }

    @Override
    public String toString() {
        return "c.q.l.co.rolling.helper.RenameUtil";
    }
}

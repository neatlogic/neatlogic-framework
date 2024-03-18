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

import ch.qos.logback.core.util.FileSize;
import neatlogic.framework.file.core.recovery.ResilientFileOutputStream;
import neatlogic.framework.file.core.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * FileAppender将日志事件附加到文件。
 * @param <E>
 */
public class FileAppender<E> extends OutputStreamAppender<E> {
    private Logger logger = LoggerFactory.getLogger(FileAppender.class);
    public static final long DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 附加或截断文件？此变量的默认值为true，这意味着默认情况下，FileAppender将附加到现有文件，而不会截断该文件。
     */
    protected boolean append = true;

    /**
     * 当前激活（未归档）文件的名称。
     */
    protected String fileName = null;

    private FileSize bufferSize = new FileSize(DEFAULT_BUFFER_SIZE);

    /**
     * 设置文件名
     */
    public void setFile(String file) {
        if (file == null) {
            fileName = file;
        } else {
            fileName = file.trim();
        }
    }

    public boolean isAppend() {
        return append;
    }

    /**
     * 派生类使用此方法获取原始文件属性。普通用户不应该调用此方法。
     */
    final public String rawFileProperty() {
        return fileName;
    }

    public String getFile() {
        return fileName;
    }

    public void start() {
        int errors = 0;
        if (getFile() != null) {

            // 仅当无冲突时才应打开文件
            try {
                openFile(getFile());
            } catch (IOException e) {
                errors++;
                logger.error("打开[" + fileName + "]文件失败");
                logger.error(e.getMessage(), e);
            }
        } else {
            errors++;
            logger.error("没有设置追加器名称");
        }
        if (errors == 0) {
            super.start();
        }
    }

    @Override
    public void stop() {
        super.stop();
    }

    /**
     * 设置并打开日志输出将放在的文件。指定的文件必须是可写的。
     * 如果已经有一个打开的文件，则先关闭前一个文件。
     * 不要直接使用此方法。要配置FileAppender或其子类之一，请逐个设置其属性，然后调用start（）。
     *
     * @param file_name 文件的路径。
     */
    public void openFile(String file_name) throws IOException {
        lock.lock();
        try {
            File file = new File(file_name);
            boolean result = FileUtil.createMissingParentDirectories(file);
            if (!result) {
                logger.error("无法为[" + file.getAbsolutePath() + "]创建父目录");
            }

            ResilientFileOutputStream resilientFos = new ResilientFileOutputStream(file, append, bufferSize.getSize());
            setOutputStream(resilientFos);
        } finally {
            lock.unlock();
        }
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public void setBufferSize(FileSize bufferSize) {
        this.bufferSize = bufferSize;
    }

    private void safeWrite(E event) throws IOException {
        ResilientFileOutputStream resilientFOS = (ResilientFileOutputStream) getOutputStream();
        FileChannel fileChannel = resilientFOS.getChannel();
        if (fileChannel == null) {
            return;
        }

        // 清除任何当前中断
        boolean interrupted = Thread.interrupted();

        FileLock fileLock = null;
        try {
            fileLock = fileChannel.lock();
            long position = fileChannel.position();
            long size = fileChannel.size();
            if (size != position) {
                fileChannel.position(size);
            }
            super.writeOut(event);
        } catch (IOException e) {
            // 主要用于捕获FileLockInterruptionExceptions
            resilientFOS.postIOFailure(e);
        } finally {
            if (fileLock != null && fileLock.isValid()) {
                fileLock.release();
            }

            // 如果在中断状态下启动，请重新中断
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    protected void writeOut(E event) throws IOException {
        safeWrite(event);
    }
}

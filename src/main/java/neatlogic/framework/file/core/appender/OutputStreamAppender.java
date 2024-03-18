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

import neatlogic.framework.file.core.IEvent;
import neatlogic.framework.file.core.encoder.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

/**
 * OutputStreamAppender将事件附加到{@link OutputStream}。此类提供其他附加程序构建的基本服务。
 * @param <E>
 */
public class OutputStreamAppender<E> extends UnsynchronizedAppenderBase<E> {
    private Logger logger = LoggerFactory.getLogger(OutputStreamAppender.class);
    /**
     * 编码器最终负责将事件写入OutputStream
     */
    protected Encoder<E> encoder;

    /**
     * 此类中的所有同步都是通过lock对象完成的。
     */
    protected final ReentrantLock lock = new ReentrantLock(false);

    /**
     * 此追加器使用的基础输出流。
     */
    private OutputStream outputStream;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * 检查是否需要设置参数，如果一切正常，则激活此追加器。
     */
    public void start() {
        int errors = 0;
        if (this.encoder == null) {
            logger.error("没有为名为[" + name + "]的追加器设置编码器");
            errors++;
        }

        if (this.outputStream == null) {
            logger.error("没有为名为[" + name + "]的追加器设置输出流");
            errors++;
        }
        // 只应激活无错误的追加器
        if (errors == 0) {
            super.start();
        }
    }

    @Override
    protected void append(E eventObject) {
//        System.out.println(2);
        if (!isStarted()) {
            return;
        }

        subAppend(eventObject);
    }

    /**
     * 停止此appender实例。底层流或编写器也已关闭。
     * 停止的追加器无法重复使用。
     */
    public void stop() {
        lock.lock();
        try {
            closeOutputStream();
            super.stop();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 关闭底层输出流
     */
    protected void closeOutputStream() {
        if (this.outputStream != null) {
            try {
                encoderClose();
                this.outputStream.close();
                this.outputStream = null;
            } catch (IOException e) {
                logger.error("无法关闭OutputStreamAppender的输出流。");
            }
        }
    }

    void encoderInit() {
        if (encoder != null && this.outputStream != null) {
            try {
                byte[] header = encoder.headerBytes();
                writeBytes(header);
            } catch (IOException ioe) {
                this.started = false;
                logger.error("未能初始化名为[" + name + "]的追加器的编码器。");
                logger.error(ioe.getMessage(), ioe);
            }
        }
    }

    void encoderClose() {
        if (encoder != null && this.outputStream != null) {
            try {
                byte[] footer = encoder.footerBytes();
                writeBytes(footer);
            } catch (IOException ioe) {
                this.started = false;
                logger.error("无法为名为[" + name + "]的追加器写入页脚。");
                logger.error(ioe.getMessage(), ioe);
            }
        }
    }

    /**
     * 设置一个已打开的输出流。
     */
    public void setOutputStream(OutputStream outputStream) {
        lock.lock();
        try {
            // 关闭任何以前打开的输出流
            closeOutputStream();
            this.outputStream = outputStream;
            if (encoder == null) {
                logger.warn("尚未设置编码器。无法调用其init方法。");
                return;
            }
            encoderInit();
        } finally {
            lock.unlock();
        }
    }

    protected void writeOut(E event) throws IOException {
        byte[] byteArray = this.encoder.encode(event);//PatternLayoutEncoder
        writeBytes(byteArray);
    }

    private void writeBytes(byte[] byteArray) throws IOException {
//        System.out.println(5);
        if(byteArray == null || byteArray.length == 0)
            return;

        lock.lock();
        try {
            //ResilientFileOutputStream
            this.outputStream.write(byteArray);
            this.outputStream.flush();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 实际写入发生在此处。
     * 大多数子类都需要重写此方法。
     */
    protected void subAppend(E e) {
//        System.out.println(4);
        if (!isStarted()) {
            return;
        }
        try {
            if (e instanceof IEvent) {
                IEvent event = ((IEvent) e);
                event.preProcessor();
                event.prepareForDeferredProcessing();
            }
            writeOut(e);

        } catch (IOException ioe) {
            // 一旦发生异常，立即转到非启动状态。
            this.started = false;
            logger.error("追加器中的IO失败");
            logger.error(ioe.getMessage(), ioe);
        }
    }

    public Encoder<E> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }
}

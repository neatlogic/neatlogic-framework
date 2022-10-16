/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.appender;

import ch.qos.logback.core.rolling.RolloverFailure;
import codedriver.framework.file.core.IEvent;
import codedriver.framework.file.core.rolling.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class RollingFileAppender<E> extends FileAppender<E> {
    private Logger logger = LoggerFactory.getLogger(RollingFileAppender.class);
    File currentlyActiveFile;
    TriggeringPolicy<E> triggeringPolicy;
    RollingPolicy rollingPolicy;

    public void start() {
        if (triggeringPolicy == null) {
            logger.warn("没有为名为" + getName() + "的RollingFileAppender设置TriggeringPolicy");
            return;
        }
        if (!triggeringPolicy.isStarted()) {
            logger.warn("TriggeringPolicy尚未启动。RollingFileAppender将不会启动");
            return;
        }

        // 我们不想使现有的日志文件无效
        if (!append) {
            logger.warn("附加模式对于RollingFileAppender是必需的。默认为append=true。");
            append = true;
        }

        if (rollingPolicy == null) {
            logger.warn("没有为名为" + getName() + "RollingPolicy");
            return;
        }

        if (checkForFileAndPatternCollisions()) {
            logger.error("File属性与fileNamePattern冲突。正在中止。");
            return;
        }

//        if (isPrudent()) {
//            if (rawFileProperty() != null) {
//                logger.warn("由于谨慎模式，将“File”属性设置为null");
//                setFile(null);
//            }
//            if (rollingPolicy.getCompressionMode() != CompressionMode.NONE) {
//                logger.error("谨慎模式下不支持压缩。正在中止");
//                return;
//            }
//        }

        currentlyActiveFile = new File(getFile());
        super.start();
    }

    /**
     * 检查文件和模式是否冲突
     * @return
     */
    private boolean checkForFileAndPatternCollisions() {
        if (triggeringPolicy instanceof RollingPolicyBase) {
            final RollingPolicyBase base = (RollingPolicyBase) triggeringPolicy;
            final FileNamePattern fileNamePattern = base.getFileNamePattern();
            // 如果fileName或fileNamePattern为空，则检查没有用
            if (fileNamePattern != null && fileName != null) {
                String regex = fileNamePattern.toRegex();
                System.out.println("regex=" + regex);
                return fileName.matches(regex);
            }
        }
        return false;
    }

    @Override
    public void stop() {
        super.stop();

        if (rollingPolicy != null)
            rollingPolicy.stop();
        if (triggeringPolicy != null)
            triggeringPolicy.stop();
    }

    @Override
    public void setFile(String file) {
        // 如果谨慎模式要求，允许将文件名设置为null
        if (file != null && ((triggeringPolicy != null) || (rollingPolicy != null))) {
            logger.error("必须在任何triggeringPolicy或rollingPolicy属性之前设置File属性");
        }
        super.setFile(file);
    }

    @Override
    public String getFile() {
        return rollingPolicy.getActiveFileName();
    }

    /**
     * Implemented by delegating most of the rollover work to a rolling policy.
     * 通过将大部分滚动工作委派给滚动策略来实现。
     */
    public void rollover() {
        lock.lock();
        try {
            // 注意：此方法需要同步，因为它需要在关闭后重新打开目标文件时进行独占访问。
            // 请确保关闭此处的活动日志文件！在窗口下重命名不适用于打开的文件。
            this.closeOutputStream();
            attemptRollover();
            attemptOpenFile();
        } finally {
            lock.unlock();
        }
    }

    private void attemptOpenFile() {
        try {
            // 更新当前活动文件
            currentlyActiveFile = new File(rollingPolicy.getActiveFileName());

            // 这也将关闭文件。这是正常的，因为多次关闭操作是安全的。
            this.openFile(rollingPolicy.getActiveFileName());
        } catch (IOException e) {
            logger.error("打开文件[" + fileName + "]失败");
        }
    }

    private void attemptRollover() {
        try {
            rollingPolicy.rollover();
        } catch (RolloverFailure rf) {
            logger.warn("发生RolloverFailure。推迟翻滚。");
            // 我们无法滚动，让我们不要截断并冒数据丢失的风险
            this.append = true;
        }
    }

    /**
     * 此方法将RollingFileAppender与其基类区分开来。实现滚动归档
     */
    @Override
    protected void subAppend(E e) {
//        System.out.println(3);
        boolean rollover = false;
        // 滚动检查必须在实际写入之前进行。这是时间驱动触发器的唯一正确行为。
        // 我们需要在triggeringPolicy上同步，以便一次只发生一次滚动
        synchronized (triggeringPolicy) {
//            System.out.println("triggeringPolicy:" + triggeringPolicy.getClass().getName());//ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
            if (triggeringPolicy.isTriggeringEvent(currentlyActiveFile, e)) {
//                System.out.println("rollover()");
                rollover();
                rollover = true;
            }
        }
        long length = currentlyActiveFile.length();
        System.out.println("before activeFileLength=" + currentlyActiveFile.length());
        super.subAppend(e);
        System.out.println("after activeFileLength=" + currentlyActiveFile.length());
        if (e instanceof IEvent) {
            IEvent event = (IEvent) e;
            event.setBeforeAppendFileSize(length);
            event.setRollover(rollover);
            event.getData().put("path", currentlyActiveFile.getAbsolutePath());
            event.postProcessor();
        }
    }

    public RollingPolicy getRollingPolicy() {
        return rollingPolicy;
    }

    public TriggeringPolicy<E> getTriggeringPolicy() {
        return triggeringPolicy;
    }

    @SuppressWarnings("unchecked")
    public void setRollingPolicy(RollingPolicy policy) {
        rollingPolicy = policy;
        if (rollingPolicy instanceof TriggeringPolicy) {
            triggeringPolicy = (TriggeringPolicy<E>) policy;
        }

    }

    public void setTriggeringPolicy(TriggeringPolicy<E> policy) {
        triggeringPolicy = policy;
        if (policy instanceof RollingPolicy) {
            rollingPolicy = (RollingPolicy) policy;
        }
    }
}

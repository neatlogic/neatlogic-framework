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

import ch.qos.logback.core.rolling.RolloverFailure;
import neatlogic.framework.file.core.IEvent;
import neatlogic.framework.file.core.rolling.RollingPolicy;
import neatlogic.framework.file.core.rolling.TriggeringPolicy;
import neatlogic.framework.file.core.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

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

        try {
            // currentlyActiveFile如果存在，正常应该是软连接文件
            currentlyActiveFile = new File(getFile());
            if (currentlyActiveFile.exists()) {
                Path linkPath = currentlyActiveFile.toPath();
                if (Files.isSymbolicLink(linkPath)) {
                    Path targetPath = Files.readSymbolicLink(linkPath);
                    if (targetPath != null) {
                        File targetFile = targetPath.toFile();
                        if (!targetFile.exists()) {
                            // 如果软链接的目标文件被删除，则新建一个文件
                            targetFile.createNewFile();
                        }
                    }
                } else {
                    currentlyActiveFile.delete();
                    // 如果currentlyActiveFile文件不存在，则找到当前目标文件，并创建软链接
                    File targetFile = getCurrentlyTargetFile();
                    if (!targetFile.exists()) {
                        targetFile.createNewFile();
                    }
                    Path targetPath = targetFile.toPath();
                    Files.createSymbolicLink(linkPath, targetPath);
                }
            } else {
                // 如果currentlyActiveFile文件不存在，则找到当前目标文件，并创建软链接
                File targetFile = getCurrentlyTargetFile();
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                }
                Path targetPath = targetFile.toPath();
                Path linkPath = currentlyActiveFile.toPath();
                Files.createSymbolicLink(linkPath, targetPath);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        super.start();
    }

    /**
     * 查找当前目标文件，
     * 如果软链接文件所在的目录是空的，则目标文件下标是最小下标
     * 如果软链接文件所在的目录不是空的，则目标文件是修改时间最晚的那个文件
     * @return
     */
    private File getCurrentlyTargetFile() {
        if (!currentlyActiveFile.exists()) {
            FileUtil.createMissingParentDirectories(currentlyActiveFile);
        }
        File parentFile = currentlyActiveFile.getParentFile();
        File[] listFiles = parentFile.listFiles();
        if (listFiles == null || listFiles.length == 0) {
            return new File(getFile() + "." + rollingPolicy.getMinIndex());
        }
        Arrays.sort(listFiles, Comparator.comparingLong(File::lastModified));
        return listFiles[listFiles.length - 1];
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
            int nextIndex = attemptRollover();
            if (nextIndex != -1) {
                attemptOpenFile(nextIndex);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 尝试打开新目标文件，并创建软链接
     * @param currentIndex 新目标文件下标
     */
    private void attemptOpenFile(int currentIndex) {
        try {
            // 更新当前活动文件
            currentlyActiveFile = new File(rollingPolicy.getActiveFileName());
            // 如果软链接已存在，一定要删除，否则会抛异常
            if (currentlyActiveFile.exists()) {
                currentlyActiveFile.delete();
            }
            File currentTargetFile = new File(rollingPolicy.getActiveFileName() + "." + currentIndex);
            if (!currentTargetFile.exists()) {
                currentTargetFile.createNewFile();
            }
            Path target = currentTargetFile.toPath();
            Path link = currentlyActiveFile.toPath();
            Files.createSymbolicLink(link, target);
            // 这也将关闭文件。这是正常的，因为多次关闭操作是安全的。
            this.openFile(rollingPolicy.getActiveFileName());
        } catch (IOException e) {
            logger.error("打开文件[" + fileName + "]失败");
        }
    }

    /**
     * 尝试滚动归档
     * @return 返回成功后，下一个目标文件的下标
     */
    private int attemptRollover() {
        try {
            Path path = currentlyActiveFile.toPath();
            String absolutePath = path.toString();
            if (Files.isSymbolicLink(path)) {
                Path targetPath = Files.readSymbolicLink(path);
                File targetFile = targetPath.toFile();
                absolutePath = targetFile.getAbsolutePath();
            }
            int lastIndex = absolutePath.lastIndexOf(".");
            String currentIndexStr = absolutePath.substring(lastIndex + 1);
            int currentIndex = Integer.parseInt(currentIndexStr);
            return rollingPolicy.rollover(currentIndex);
        } catch (RolloverFailure | IOException rf) {
            logger.warn("发生RolloverFailure。推迟翻滚。");
            // 我们无法滚动，让我们不要截断并冒数据丢失的风险
            this.append = true;
        }
        return -1;
    }

    /**
     * 此方法将RollingFileAppender与其基类区分开来。实现滚动归档
     */
    @Override
    protected void subAppend(E e) {
//        System.out.println(3);
        // 滚动检查必须在实际写入之前进行。这是时间驱动触发器的唯一正确行为。
        // 我们需要在triggeringPolicy上同步，以便一次只发生一次滚动
        synchronized (triggeringPolicy) {
            //SizeBasedTriggeringPolicy
            if (triggeringPolicy.isTriggeringEvent(currentlyActiveFile, e)) {
                rollover();
            }
        }
        long length = currentlyActiveFile.length();
        super.subAppend(e);
        if (e instanceof IEvent) {

            Path linkPath = currentlyActiveFile.toPath();
            String path = linkPath.toString();
            if (Files.isSymbolicLink(linkPath)) {
                try {
                    Path targetPath = Files.readSymbolicLink(linkPath);
                    path = targetPath.toString();
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
            IEvent event = (IEvent) e;
            event.setBeforeAppendFileSize(length);
            event.getData().put("path", path);
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

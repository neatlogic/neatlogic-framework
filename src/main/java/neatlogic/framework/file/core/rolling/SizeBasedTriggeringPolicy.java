/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.file.core.rolling;

import ch.qos.logback.core.util.FileSize;
import neatlogic.framework.file.core.util.DefaultInvocationGate;
import neatlogic.framework.file.core.util.InvocationGate;

import java.io.File;

/**
 * SizeBasedTriggeringPolicy查看当前写入的文件的大小。
 * 如果文件的大小超过指定的大小，则使用SizeBased Triggering Policy的FileAppender将滚动文件并创建新文件。
 */
public class SizeBasedTriggeringPolicy<E> extends TriggeringPolicyBase<E> {
    /**
     * 默认最大文件大小。
     */
    public static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    FileSize maxFileSize = new FileSize(DEFAULT_MAX_FILE_SIZE);

    public SizeBasedTriggeringPolicy() {
    }

    InvocationGate invocationGate = new DefaultInvocationGate();

    public boolean isTriggeringEvent(final File activeFile, final E event) {
        long now = System.currentTimeMillis();
        if (invocationGate.isTooSoon(now)) {
//            System.out.println("isTooSoon: " + true);
            return false;
        }
        return (activeFile.length() >= maxFileSize.getSize());
    }

    public void setMaxFileSize(FileSize aMaxFileSize) {
        this.maxFileSize = aMaxFileSize;
    }

}

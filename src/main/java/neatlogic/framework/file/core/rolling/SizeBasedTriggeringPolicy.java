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

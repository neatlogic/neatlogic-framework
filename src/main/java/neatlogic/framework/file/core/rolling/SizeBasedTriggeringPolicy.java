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

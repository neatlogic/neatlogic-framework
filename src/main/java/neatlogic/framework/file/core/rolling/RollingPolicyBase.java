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

import neatlogic.framework.file.core.appender.FileAppender;

/**
 * 实现大多数（并非所有）滚动策略通用的方法。目前，此类方法仅限于压缩模式的getter/setter。
 */
public abstract class RollingPolicyBase implements RollingPolicy {

    private FileAppender<?> parent;

    private boolean started;

    public boolean isStarted() {
        return started;
    }

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public void setParent(FileAppender<?> appender) {
        this.parent = appender;
    }

    public String getParentsRawFileProperty() {
        return parent.rawFileProperty();
    }
}

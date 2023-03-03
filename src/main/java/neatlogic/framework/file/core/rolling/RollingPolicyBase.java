/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

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

/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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

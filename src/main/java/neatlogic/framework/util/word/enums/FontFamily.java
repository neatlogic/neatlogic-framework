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
package neatlogic.framework.util.word.enums;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

/**
 * @author longrf
 * @date 2022/9/26 17:56
 */

public enum FontFamily {
    SONG(new I18n("宋体")),
    BLACK(new I18n("黑体")),
    FANG_SONG(new I18n("仿宋")),
    REGULAR_SCRIPT(new I18n("楷体")),
    ;

    private final I18n value;

    private FontFamily(I18n value) {
        this.value = value;
    }

    public String getValue() {
        return $.t(value.toString());
    }
}

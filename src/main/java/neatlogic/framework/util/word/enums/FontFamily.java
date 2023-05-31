/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package neatlogic.framework.util.word.enums;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

/**
 * @author longrf
 * @date 2022/9/26 17:56
 */

public enum FontFamily {
    SONG(new I18n("enum.framework.fontfamily.song")),
    BLACK(new I18n("enum.framework.fontfamily.black")),
    FANG_SONG(new I18n("enum.framework.fontfamily.fang_song")),
    REGULAR_SCRIPT(new I18n("common.regularscript")),
    ;

    private final I18n value;

    private FontFamily(I18n value) {
        this.value = value;
    }

    public String getValue() {
        return I18nUtils.getMessage(value.toString());
    }
}

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
 * @date 2022/9/26 17:47
 */

public enum TableColor {
    BLACK("000000", new I18n("common.black")),
    WHITE("ffffff", new I18n("common.white")),
    RED("FF0000", new I18n("common.red")),
    BLUE("0000FF", new I18n("common.blue")),
    GREY("808080", new I18n("enum.framework.tablecolor.grey")),
    ;

    private final String value;
    private final I18n text;

    private TableColor(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }
}

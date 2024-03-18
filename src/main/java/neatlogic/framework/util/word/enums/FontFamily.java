/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

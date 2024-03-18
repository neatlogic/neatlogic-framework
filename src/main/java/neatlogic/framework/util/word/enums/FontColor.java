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
 * @date 2022/9/26 17:47
 */

public enum FontColor {
    BLACK("000000", new I18n("黑色")),
    WHITE("ffffff", new I18n("白色")),
    RED("FF0000", new I18n("红色")),
    BLUE("0000FF", new I18n("蓝色")),
    ;

    private final String value;
    private final I18n text;

    private FontColor(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }
}

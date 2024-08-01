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

package neatlogic.framework.datawarehouse.enums;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum Status {
    DOING("doing", new I18n("同步数据中")),
    DONE("done", new I18n("同步完成")),
    ABORTED("aborted", new I18n("同步已中止")),
    FAILED("failed", new I18n("同步失败"));

    private final String value;
    private final I18n text;

    Status(String _value, I18n _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }


    public static String getText(String name) {
        for (Status s : Status.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}

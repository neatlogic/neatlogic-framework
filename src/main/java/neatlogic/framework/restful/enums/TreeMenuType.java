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

package neatlogic.framework.restful.enums;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum TreeMenuType {
    SYSTEM("system", new I18n("系统接口目录")),
    CUSTOM("custom", new I18n("自定义接口目录")),
    AUDIT("audit", new I18n("操作审计目录"));

    private final String name;
    private final I18n text;

    TreeMenuType(String _name, I18n _text) {
        this.name = _name;
        this.text = _text;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return $.t(text.toString());

    }

    public static String getText(String name) {
        for (TreeMenuType s : TreeMenuType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}

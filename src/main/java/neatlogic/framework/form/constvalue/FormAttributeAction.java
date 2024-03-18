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

package neatlogic.framework.form.constvalue;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum FormAttributeAction {
    HIDE("hide", new I18n("隐藏")),
    READ("read", new I18n("只读")),
    EDIT("edit", new I18n("编辑"));
    private String value;
    private I18n text;

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }

    private FormAttributeAction(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

}

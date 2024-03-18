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

package neatlogic.framework.matrix.constvalue;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum SearchExpression {
    EQ("eq", "equal", new I18n("等于")),
    BT("bt", "between", new I18n("在此区间")),
    NE("ne", "notequal", new I18n("不等于")),
    NL("nl", "notlike", new I18n("不包含")),
    LI("li", "like", new I18n("包含")),
    NULL("null", "is-null", new I18n("为空")),
    NOTNULL("notnull", "is-not-null", new I18n("不为空"));

    private final String value;
    private final I18n text;
    private final String expression;

    SearchExpression(String value, String expression, I18n text) {
        this.value = value;
        this.text = text;
        this.expression = expression;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }

    public String getExpression() {
        return expression;
    }
}

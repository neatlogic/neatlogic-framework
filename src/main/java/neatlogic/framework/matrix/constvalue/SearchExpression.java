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

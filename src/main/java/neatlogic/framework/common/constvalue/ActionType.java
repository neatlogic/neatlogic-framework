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

package neatlogic.framework.common.constvalue;

import neatlogic.framework.util.$;

/**
 * @Author:linbq
 * @Time:2020年8月18日
 * @ClassName: ActionType
 * @Description: 操作类型枚举类
 */
public enum ActionType {
    CREATE("create", "common.create"),
    UPDATE("update", "common.edit");
    private final String value;
    private final String text;

    private ActionType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text);
    }

    public static String getText(String value) {
        for (ActionType type : values()) {
            if (type.getValue().equals(value)) {
                return type.getText();
            }
        }
        return "";
    }
}

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

import java.util.List;


public interface IEnum<T> {
    /**
     * 不同的枚举类，返回不同的枚举值，可自由组合成List<>或者JSONArray
     *
     * @return 枚举列表
     */
    List<T> getValueTextList();

    default String getValue() {
        return null;
    }

    default String getEnumName() {
        return "";
    }
}

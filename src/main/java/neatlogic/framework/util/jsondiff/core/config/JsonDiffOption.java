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

package neatlogic.framework.util.jsondiff.core.config;

import neatlogic.framework.util.jsondiff.common.model.JsonComparedOption;
import neatlogic.framework.util.jsondiff.core.utils.JsonNeatFactory;

public class JsonDiffOption {

    /**
     * 全局配置
     */
    private static JsonComparedOption globallyUniqueOption = new JsonComparedOption();

    /**
     * 是否使用全局配置
     */
    private static boolean uniqueOption = false;

    /**
     * 默认的比较器工厂
     */
    private final static JsonNeatFactory jsonNeatFactory = new JsonNeatFactory();

    public static JsonNeatFactory getJsonNeatFactory() {
        return jsonNeatFactory;
    }


    public static JsonComparedOption getGloballyUniqueOption() {
        if (globallyUniqueOption == null) {
            return new JsonComparedOption();
        }
        return globallyUniqueOption;
    }

    public static void setGloballyUniqueOption(JsonComparedOption globallyUniqueOption) {
        JsonDiffOption.globallyUniqueOption = globallyUniqueOption;
    }

    public static boolean isUniqueOption() {
        return uniqueOption;
    }

    public static void openUniqueOption() {
        JsonDiffOption.uniqueOption = true;
    }

    public static void closeUniqueOption() {
        JsonDiffOption.uniqueOption = false;
    }
}

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

package neatlogic.framework.util.jsondiff.core.utils;


import neatlogic.framework.util.jsondiff.common.model.JsonComparedOption;
import neatlogic.framework.util.jsondiff.core.config.JsonDiffOption;

public class RunTimeDataFactory {


    /**
     * 配置
     */
    private final static ThreadLocal<JsonComparedOption> optionThreadLocal = new ThreadLocal<>();

    /**
     * 获取对比配置
     *
     * @return 对比配置
     */
    public static JsonComparedOption getOptionInstance() {
        if (JsonDiffOption.isUniqueOption()) {
            return JsonDiffOption.getGloballyUniqueOption();
        }
        if (optionThreadLocal.get() == null) {
            optionThreadLocal.set(new JsonComparedOption());
        }
        // 默认配置
        return optionThreadLocal.get();
    }

    /**
     * s设置对比配置
     *
     * @param jsonComparedOption 对比配置
     */
    public static void setOptionInstance(JsonComparedOption jsonComparedOption) {
        if (jsonComparedOption == null) {
            return;
        }
        optionThreadLocal.remove();
        optionThreadLocal.set(jsonComparedOption);
    }

    /**
     * 清理ThreadLocal
     */
    public static void clearOptionInstance() {
        optionThreadLocal.remove();
    }

}
